package de.jawb.keysafe.backend.app.conf;

import de.jawb.keysafe.backend.core.AccessData;
import de.jawb.keysafe.backend.core.Keysafe;
import de.jawb.keysafe.backend.core.service.daos.KeysafeDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

@Service
public class KeysafeUserDetailService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(KeysafeUserDetailService.class);

    @Inject
    private KeysafeDao dao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername: {}", username);

        AccessData accessData = dao.findByAccessUsername(username);

        if(accessData == null){
            return null;
        }

        return new User(accessData);
    }

    private record User(AccessData accessData) implements UserDetails {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return accessData.encryptedPassword();
        }

        @Override
        public String getUsername() {
            return accessData.userName();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
