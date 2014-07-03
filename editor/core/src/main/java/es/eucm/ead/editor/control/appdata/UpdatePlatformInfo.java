/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.eucm.ead.editor.control.appdata;

import javax.annotation.Generated;


/**
 * Simple object for pairing the installer url with an os version
 * 
 */
@Generated("org.jsonschema2pojo")
public class UpdatePlatformInfo {

    /**
     * Simple enum that stores platform-os types. To be used by UpdateInfo and ReleaseInfo. More info: https://github.com/e-ucm/ead/wiki/Platform-version-names
     * 
     */
    private OS os = OS.fromValue("multiplatform");
    /**
     * The appropriate url for downloading the installer for this particular os
     * 
     */
    private String url;

    /**
     * Simple enum that stores platform-os types. To be used by UpdateInfo and ReleaseInfo. More info: https://github.com/e-ucm/ead/wiki/Platform-version-names
     * 
     */
    public OS getOs() {
        return os;
    }

    /**
     * Simple enum that stores platform-os types. To be used by UpdateInfo and ReleaseInfo. More info: https://github.com/e-ucm/ead/wiki/Platform-version-names
     * 
     */
    public void setOs(OS os) {
        this.os = os;
    }

    /**
     * The appropriate url for downloading the installer for this particular os
     * 
     */
    public String getUrl() {
        return url;
    }

    /**
     * The appropriate url for downloading the installer for this particular os
     * 
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
