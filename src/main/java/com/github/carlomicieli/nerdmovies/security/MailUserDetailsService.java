/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.carlomicieli.nerdmovies.security;

import com.github.carlomicieli.nerdmovies.models.MailUser;
import com.github.carlomicieli.nerdmovies.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Carlo Micieli
 */
@Service("mailUserDetailsService")
public class MailUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Autowired
    public MailUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress)
            throws UsernameNotFoundException {

        MailUser user = userService.findUserByEmail(emailAddress);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with username '" + emailAddress + "'");
        }
        return new MailUserDetails(user);
    }
}
