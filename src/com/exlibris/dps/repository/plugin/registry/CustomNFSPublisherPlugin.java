package com.exlibris.dps.repository.plugin.registry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.exlibris.core.infra.common.exceptions.logging.ExLogger;
import com.exlibris.core.sdk.strings.StringUtils;
import com.exlibris.core.sdk.utils.FileUtil;
import com.exlibris.dps.sdk.registry.PublisherRegistryPlugin;

/**
 * @author Idop
 * Publisher to NFS
 */

public class CustomNFSPublisherPlugin implements PublisherRegistryPlugin {


	// Folder parameter name
	protected static final String DIRECTORY = "directory";
	protected static final String SUB_DIRECTORIES = "subDirs";
	protected static final String EXTENSION = "extension";

	protected static final String DEFAULT_EXTENSION = "xml";

	// ExLibris Logger
	private static ExLogger log = ExLogger.getExLogger(CustomNFSPublisherPlugin.class);

	// Publisher initial parameters
	private Map<String, String> initParams = new HashMap<String, String>();
	private String directory;
	private int subDirs;
	private String extension;


	/* **************************************************************************
	 * @see com.exlibris.core.infra.model.PublisherRegistry#publish(java.lang.String, java.lang.String)
	 * This method writes the converted IE to the NFS
	 ************************************************************************* */
	public boolean publish(String pid, String convertedIE) {
		String path = this.directory + File.separator + Math.abs(pid.hashCode() % subDirs) + File.separator + pid + "." + this.extension;
		try {
			FileUtil.writeFile(new File(path), convertedIE);
			return true;
		} catch (Exception e) {
			return false;
		}
	}



	/* **********************************************************************************
	 * @see com.exlibris.core.infra.model.PublisherRegistry#unpublish(java.lang.String)
	 * The method removes the file from the NFS if exists
	 **********************************************************************************/
	public boolean unpublish(String pid) {
		String path = this.directory + File.separator + Math.abs(pid.hashCode() % subDirs) + File.separator + pid + "." + this.extension;
		try {
			if (new File(path).delete()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}




	/***************************************************************************
	 * @see com.exlibris.core.infra.model.UtilityRegistry#initParam(java.util.Map)
	 **************************************************************************/
	public void initParam(Map<String, String> params) {
		// copy all parameters to local variable
		initParams.putAll(params);

		this.directory = initParams.get(DIRECTORY);
		if (StringUtils.isEmptyString(directory)) {
			log.error("Directory parameter not found in init parameters");
			return;
		}

		try {
			this.subDirs = Integer.parseInt(initParams.get(SUB_DIRECTORIES));
		} catch (Exception e) {
			this.subDirs = 1;
		}

		this.extension = initParams.get(EXTENSION);
		if (StringUtils.isEmptyString(extension)) {
			this.extension = DEFAULT_EXTENSION;
		}
	}
}
