package de.jawb.keysafe.backend.integration.persistence.backup;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles_revisions")
public class DbProfileRevisionEntity {

	@Id
	@GeneratedValue
    private UUID    id;

	@Column(name = "profile_id")
	private UUID profileId;

	@Column(name = "safe_id")
	private UUID safeId;

	@Column(name = "profile_name")
	private String profileName;

	@Column(name = "data_date")
	private LocalDateTime dataDate;

	@Column(name = "data_checksum")
	private String dataChecksum;

	@Column(name = "data")
	private String data;

	public UUID getProfileId() {
		return profileId;
	}

	public void setProfileId(UUID profileId) {
		this.profileId = profileId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getSafeId() {
		return safeId;
	}

	public void setSafeId(UUID safeId) {
		this.safeId = safeId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public LocalDateTime getDataDate() {
		return dataDate;
	}

	public void setDataDate(LocalDateTime backupDate) {
		this.dataDate = backupDate;
	}

	public String getDataChecksum() {
		return dataChecksum;
	}

	public void setDataChecksum(String checksum) {
		this.dataChecksum = checksum;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "DbProfileRevisionEntity{" +
				"id=" + id +
				", profileId=" + profileId +
				", safeId=" + safeId +
				", profileName='" + profileName + '\'' +
				", dataDate=" + dataDate +
				", dataChecksum='" + dataChecksum + '\'' +
				", data='...'" +
				'}';
	}
}
