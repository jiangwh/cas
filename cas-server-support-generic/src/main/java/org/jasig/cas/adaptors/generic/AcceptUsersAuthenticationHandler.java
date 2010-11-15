/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.cas.adaptors.generic;

import java.util.Collections;
import java.util.Map;

import org.jasig.cas.server.authentication.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.server.authentication.UserNamePasswordCredential;

import javax.validation.constraints.NotNull;

/**
 * Handler that contains a list of valid users and passwords. Useful if there is
 * a small list of users that we wish to allow. An example use case may be if
 * there are existing handlers that make calls to LDAP, etc. but there is a need
 * for additional users we don't want in LDAP. With the chain of command
 * processing of handlers, this handler could be added to check before LDAP and
 * provide the list of additional users. The list of acceptable users is stored
 * in a map. The key of the map is the username and the password is the object
 * retrieved from doing map.get(KEY).
 * <p>
 * Note that this class makes an unmodifiable copy of whatever map is provided
 * to it.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0
 */
public final class AcceptUsersAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    /** The list of users we will accept. */
    @NotNull
    private Map<String, String> users;

    protected final boolean authenticateUsernamePasswordInternal(final UserNamePasswordCredential credentials) {
        final String transformedUsername = getPrincipalNameTransformer().transform(credentials.getUserName());
        final String cachedPassword = this.users.get(transformedUsername);

        if (cachedPassword == null) {
            if (log.isDebugEnabled()) {
                log.debug("The user [" + transformedUsername
                    + "] was not found in the map.");
            }
            return false;
        }

        return this.getPasswordEncoder().isValidPassword(cachedPassword, credentials.getPassword(), null);
    }

    /**
     * @param users The users to set.
     */
    public void setUsers(final Map<String, String> users) {
        this.users = Collections.unmodifiableMap(users);
    }
}