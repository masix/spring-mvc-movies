/*
Copyright [2012] [Carlo P. Micieli]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.github.carlomicieli.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.carlomicieli.models.MailUser;

@Service("securityService")
public class SpringSecurityService implements SecurityService {

	@Override
	public void autenticate(MailUser user) {
		MailUserDetails det = new MailUserDetails(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(det,
				det.getPassword(),
				det.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Override
	public MailUser getCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

}
