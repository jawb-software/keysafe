package de.jawb.keysafe.backend.integration.persistence.backup;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "safes")
public class DbKeysafeEntity {

	@Id
	@GeneratedValue
    private UUID    id;

	@Column(name = "name")
	private String safeName;

	@Column(name = "access_username")
	private String accessUserName;

	@Column(name = "access_password")
	private String accessPassword;

	@Column(name = "created")
	private LocalDateTime created;

	public String getSafeName() {
		return safeName;
	}

	public void setSafeName(String safeName) {
		this.safeName = safeName;
	}

	public String getAccessUserName() {
		return accessUserName;
	}

	public void setAccessUserName(String accessUserName) {
		this.accessUserName = accessUserName;
	}

	public String getAccessPassword() {
		return accessPassword;
	}

	public void setAccessPassword(String accessPassword) {
		this.accessPassword = accessPassword;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DbKeysafeEntity{" +
				"id=" + id +
				", safeName='" + safeName + '\'' +
				", accessUserName='" + accessUserName + '\'' +
				", accessPassword='" + accessPassword + '\'' +
				", created=" + created +
				'}';
	}
}
