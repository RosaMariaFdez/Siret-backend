package dqteam.siret.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dqteam.siret.dao.UserDAO;
import dqteam.siret.model.User;



@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    public MyUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional <User> user = userDAO.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new UserPrincipal(user.get());
    }

}
