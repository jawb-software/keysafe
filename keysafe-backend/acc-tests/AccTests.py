import json
import os
import time
import traceback
import uuid

import arrow
import requests

keysafe_id = ""
profile_id = ""
revision_id = ""

headers = {
    "Content-Type": "application/json",
    "User-Agent": "python/Acceptance-Tests",
}


base_url = os.getenv('BASE_BACKEND_URL', 'http://localhost:8080')
basic_auth_username = 'keysafe'
basic_auth_password = 'keysafe'

DATE_FORMAT = "DD.MM.YYYY, HH:mm:ss"
local = arrow.now().to('Europe/Berlin')

original_profile_backup = {
    "profileName": "Profile A",
    "data": "data",
    "dataChecksum": "abcd",
    "dataDate": '01.09.2022, 20:51:54'
}

changed_profile_backup = {
    "profileName": "Profile B",
    "data": "data 2",
    "dataChecksum": "abcdef",
    "dataDate": '01.09.2022, 21:51:54'
}


def main():
    print("Starting Acceptance Tests")

    session = requests.Session()
    session.auth = (basic_auth_username, basic_auth_password)

    try:

        print("")

        wait_till_ready()

        print("")

        create_keysafe()
        backup_profile(session)
        update_backup(session)

        check_get_profiles(session)
        check_revisions(session)
        check_revision(session)

        print("")

        check_revision_401_unauthorized_access_on_invalid_auth()

        check_create_keysafe_400_bad_request_on_empty_data(session)

        check_backup_profile_400_bad_request_on_empty_body(session)
        check_backup_profile_404_not_found_on_wrong_keysafe_id(session)

        check_update_backup_404_not_found_on_wrong_profile_id(session)
        check_update_backup_404_not_found_on_wrong_keysafe_id(session)

        check_update_backup_304_not_modified_on_unchanged_data(session)

        print("Acceptance Tests successfully finished")
        
    except Exception as e:
        print("\nTests failed: %s" % str(e))
        print(traceback.format_exc())


def wait_till_ready():
    api_url = base_url + "/status"

    print("- calling 'ping' GET %s" % api_url)
    tries = 0

    while tries <= 10:

        try:
            response = requests.get(api_url, headers=headers)
            status_code = response.status_code

            if status_code == 200:
                print("\t [OK] - keysafe backend is ready")
                return
            elif status_code == 401:
                print("\t [ERROR] - Bad username or password")
                return
            else:
                print("\t [ERROR] - Bad response code: %s" % status_code)

        except Exception:
            print("\t [WARN] - keysafe backend not available. try# %s" % str(tries))

        tries = tries + 1
        time.sleep(5)

    raise Exception('keysafe backend timeout')


def create_keysafe():
    api_url = base_url + "/keysafes"
    signup  = {
        "safeName": "Test",
        "accessUserName": basic_auth_username,
        "accessPassword": basic_auth_password
    }

    print("- calling 'register/create new keysafe' POST %s" % api_url)

    response = requests.post(api_url, json.dumps(signup), headers=headers)
    status_code = response.status_code

    if status_code == 201:
        response_json = response.json()

        global keysafe_id
        keysafe_id = response_json['id']

        if keysafe_id is None:
            raise Exception("response contains no keysafe id")

        print("\t [OK] - keysafe id: %s" % keysafe_id)

        return keysafe_id
    else:
        print("\t [ERROR] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def backup_profile(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles"

    print("- calling 'backup profile' POST %s + %s" % (api_url, str(original_profile_backup)))

    response = session.post(api_url, json.dumps(original_profile_backup), headers=headers)
    status_code = response.status_code

    if status_code == 201:
        backup_info = response.json()

        fields = ['id']
        for field in fields:
            if field not in backup_info:
                print("\t [ERROR] - '%s' not found in response body" % field)
                raise Exception("Missing '" + field + "' in response body " + str(backup_info))

        global profile_id
        profile_id = backup_info['id']

        print("\t [OK] - profile id: %s" % profile_id)

    else:
        print("\t [ERROR] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def update_backup(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/" + profile_id

    print("- calling 'update profile backup' PUT %s" % api_url)

    response = session.put(api_url, json.dumps(changed_profile_backup), headers=headers)
    status_code = response.status_code

    if status_code == 204:
        print("\t [OK] - profile updated")

    else:
        print("\t [Error] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_get_profiles(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/"

    print("- calling 'get profiles' GET %s" % api_url)

    response = session.get(api_url, headers=headers)
    status_code = response.status_code

    if status_code == 200:
        profiles = response.json()

        if len(profiles) != 1:
            print("\t [ERROR] - expecting 1 profile entry but got %s" % str(len(profiles)))
            raise Exception("Get Profiles Test failed")

        profile = profiles[0]

        #
        #
        #
        fields = ['profileName', 'dataChecksum', 'dataDate']
        for field in fields:
            if field not in profile:
                print("\t [ERROR] - '%s' not found in profile info object" % field)
                raise Exception("Missing '" + field + "' in profile info object " + str(profile))

            a_value = profile[field]
            b_value = changed_profile_backup[field]

            if a_value != b_value:
                print("\t [ERROR] - revision field '%s' is invalid: got '%s' expected: '%s'" % (field, str(a_value), str(b_value)))
                raise Exception("Invalid profile info: " + str(profiles))

        #
        # profile info element darf keine Daten enthalten
        #
        if 'data' in profile:
            print("\t [ERROR] - profile info object contains backup data")
            raise Exception("Found backupData in profile info object")

        if profile['profileId'] != profile_id:
            print("\t [ERROR] - profile info object has invalid uuid: got '%s' expected: '%s'")
            raise Exception("Invalid profile info object: " + str(profile))

        print("\t [OK] - profile info object exists and correct")

    else:
        print("\t [Error] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_revisions(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/" + profile_id + "/revisions"

    print("- calling 'get revisions' GET %s" % api_url)

    response = session.get(api_url, headers=headers)
    status_code = response.status_code

    if status_code == 200:
        entries = response.json()

        if len(entries) != 1:
            print("\t [ERROR] - expecting 1 history entry but got %s" % str(len(entries)))
            raise Exception("History Test failed")

        entry = entries[0]

        #
        #
        #
        fields = ['profileName', 'dataChecksum', 'dataDate']
        for field in fields:
            if field not in entry:
                print("\t [ERROR] - '%s' not found in history entry object" % field)
                raise Exception("Missing '" + field + "' in history entry object " + str(entry))

            if entry[field] != original_profile_backup[field]:
                print("\t [ERROR] - revision field '%s' is invalid: got '%s' expected: '%s'" % (field, str(entry[field]), str(original_profile_backup[field])))
                raise Exception("Invalid revision: " + str(entries))

        #
        # revision liste element darf keine Daten enthalten
        #
        if 'data' in entry:
            print("\t [ERROR] - revision obj contains backup data")
            raise Exception("Found backupData in revision obj")

        global revision_id
        revision_id = entry['revisionId']

        print("\t [OK] - revision entry exists and correct")

    else:
        print("\t [Error] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_revision(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/" + profile_id + "/revisions/" + revision_id

    print("- calling 'get revision' GET %s" % api_url)

    response = session.get(api_url, headers=headers)
    status_code = response.status_code

    if status_code == 200:
        response_body = response.json()

        fields = ['profileName', 'data', 'dataChecksum', 'dataDate']
        for field in fields:
            if field not in response_body:
                print("\t [ERROR] - '%s' not found in response body" % field)
                raise Exception("Missing '" + field + "' in response body " + str(response_body))

            if response_body[field] != original_profile_backup[field]:
                print("\t [ERROR] - revision field '%s' is invalid: got '%s' expected: '%s'" % (field, str(response_body[field]), str(original_profile_backup[field])))
                raise Exception("Invalid revision: " + str(response_body))

        print("\t [OK] - revision entry is ok")

    else:
        print("\t [Error] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_revision_401_unauthorized_access_on_invalid_auth():
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/" + profile_id + "/revisions/" + revision_id
    print("- calling unauthorized %s" % api_url)

    response = requests.get(api_url, auth=('baduser', 'badpassword'))
    status_code = response.status_code

    if status_code == 401:
        print("\t [OK] - unauthorized access failed")

    else:
        print("\t [Error] - Bad response code: %s. Expected 401" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_create_keysafe_400_bad_request_on_empty_data(session):
    api_url = base_url + "/keysafes"
    signup  = {}

    print("- calling 'create new keysafe' with empty body POST %s" % api_url)

    response = session.post(api_url, json.dumps(signup), headers=headers)
    status_code = response.status_code

    if status_code == 400:
        print("\t [OK] - 400 - Bad request on empty name")
    else:
        print("\t [ERROR] - Bad response code: %s. Expected 400." % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_backup_profile_404_not_found_on_wrong_keysafe_id(session):
    api_url = base_url + "/keysafes/" + str(uuid.uuid4()) + "/profiles"

    print("- calling 'backup profile' with invalid keysafe-id POST %s" % api_url)

    response = session.post(api_url, json.dumps(original_profile_backup), headers=headers)
    status_code = response.status_code

    if status_code == 404:
        error_response = response.json()

        _check_error_response_has_required_fields(error_response)
        _check_error_response_has_expected_reasons_count(error_response, 1)
        _check_error_response_has_expected_message(error_response, "keysafe not found")

        print("\t [OK] - got correct error message: %s" % str(error_response['reason'][0]))

    else:
        print("\t [ERROR] - Bad response code: %s. Expected 400." % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_backup_profile_400_bad_request_on_empty_body(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles"

    print("- calling 'backup profile' with empty body POST %s" % api_url)

    response = session.post(api_url, "{}", headers=headers)
    status_code = response.status_code

    if status_code == 400:
        error_response = response.json()

        _check_error_response_has_required_fields(error_response)
        _check_error_response_has_expected_reasons_count(error_response, 4)

        print("\t [OK] - got correct error message: %s" % str(error_response['reason']))

    else:
        print("\t [ERROR] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_update_backup_404_not_found_on_wrong_profile_id(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/" + str(uuid.uuid4())
    body    = {
        "profileName": "Profile B",
        "data": "data 2",
        "dataChecksum": "abcdef",
        "dataDate": '02.09.2022, 17:51:54'
    }

    print("- calling 'update profile backup' with invalid profile-id PUT %s" % api_url)

    response    = session.put(api_url, json.dumps(body), headers=headers)
    status_code = response.status_code

    if status_code == 404:
        error_response = response.json()

        _check_error_response_has_required_fields(error_response)
        _check_error_response_has_expected_reasons_count(error_response, 1)
        _check_error_response_has_expected_message(error_response, "profile not found")

        print("\t [OK] - got correct error message: %s" % str(error_response['reason'][0]))

    else:
        print("\t [ERROR] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_update_backup_404_not_found_on_wrong_keysafe_id(session):
    api_url = base_url + "/keysafes/" + str(uuid.uuid4()) + "/profiles/" + profile_id
    body    = {
        "profileName": "Profile B",
        "data": "data 2",
        "dataChecksum": "abcdef",
        "dataDate": '02.09.2022, 17:51:54'
    }

    print("- calling 'update profile backup' with invalid keysafe-id PUT %s" % api_url)

    response    = session.put(api_url, json.dumps(body), headers=headers)
    status_code = response.status_code

    if status_code == 404:
        error_response = response.json()

        _check_error_response_has_required_fields(error_response)
        _check_error_response_has_expected_reasons_count(error_response, 1)
        _check_error_response_has_expected_message(error_response, "keysafe not found")

        print("\t [OK] - got correct error message: %s" % str(error_response['reason'][0]))

    else:
        print("\t [ERROR] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def check_update_backup_304_not_modified_on_unchanged_data(session):
    api_url = base_url + "/keysafes/" + keysafe_id + "/profiles/" + profile_id

    print("- calling 'update profile backup' with unchanged data. PUT %s" % api_url)

    response = session.put(api_url, json.dumps(changed_profile_backup), headers=headers)
    status_code = response.status_code

    if status_code == 304:
        print("\t [OK] - profile not modified")

    else:
        print("\t [Error] - Bad response code: %s" % status_code)
        raise Exception('Bad response code: ' + str(status_code))


def _check_error_response_has_required_fields(error_response):
    fields = ['statusCode', 'statusMessage', 'reason']
    for field in fields:
        if field not in error_response:
            print("\t [ERROR] - '%s' not found in response body" % field)
            raise Exception("Missing '" + field + "' in response body " + str(error_response))


def _check_error_response_has_expected_reasons_count(error_response, expected_reasons: int):
    reason = error_response.get('reason')

    if len(reason) != expected_reasons:
        print("\t [ERROR] - Bad error response body. Expected %s reasons, but got: %s" % (str(expected_reasons), str(len(reason))))
        raise Exception('Bad error response body: ' + str(error_response))


def _check_error_response_has_expected_message(error_response, expected_reason):
    error_message = str(error_response['reason'][0])

    if not error_message.startswith(expected_reason):
        print("\t [ERROR] - Bad error response body. Expected '%s' reason, but got: %s" % (expected_reason, error_message))
        raise Exception('Bad error response body: ' + str(error_response))


if __name__ == "__main__":
    main()
