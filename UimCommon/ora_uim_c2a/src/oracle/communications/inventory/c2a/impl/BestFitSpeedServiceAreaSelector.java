package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.Collection;

import oracle.communications.inventory.api.entity.InventoryGroup;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.c2a.ServiceAreaBestFitSelector;

public class BestFitSpeedServiceAreaSelector implements
        ServiceAreaBestFitSelector {

	// compatibility criteria
	private long downloadSpeed = 0;
	private long uploadSpeed = 0;

	@Override
	public InventoryGroup selectBestServiceArea(
	        ServiceConfigurationVersion svcConVers,
	        Collection<InventoryGroup> areas) {
		setCompatibilityCriteria(svcConVers);
		InventoryGroup best = null;
		int bestPriority = 0;
		for (InventoryGroup a : areas) {
			if (!isCompatible(a))
				continue; // prune incompatible areas
			int aPriority = getPriority(a);
			if (best == null || aPriority > bestPriority) {
				best = a;
				bestPriority = aPriority;
			}
		}
		return best;
	}

	private void setCompatibilityCriteria(ServiceConfigurationVersion svcConVers) {
		ServiceConfigurationItem properties = ConfigurationUtils
		        .getConfigurationItem(svcConVers, "Properties");
		if (properties == null)
			return;
		String downloadSpeed = ConfigurationUtils.getConfigurationProperty(
		        properties, "DownloadSpeed");
		if (downloadSpeed != null) {
			this.downloadSpeed = BandwidthUtils.parseSpeed(downloadSpeed);
		}
		String uploadSpeed = ConfigurationUtils.getConfigurationProperty(
		        properties, "UploadSpeed");
		if (uploadSpeed != null) {
			this.uploadSpeed = BandwidthUtils.parseSpeed(uploadSpeed);
		}
	}

	private boolean isCompatible(InventoryGroup area) {
		long minDownloadSpeed = getMinDownloadSpeed(area);
		long maxDownloadSpeed = getMaxDownloadSpeed(area);
		long minUploadSpeed = getMinUploadSpeed(area);
		long maxUploadSpeed = getMaxUploadSpeed(area);
		return this.downloadSpeed >= minDownloadSpeed
		        && this.downloadSpeed <= maxDownloadSpeed
		        && this.uploadSpeed >= minUploadSpeed
		        && this.uploadSpeed <= maxUploadSpeed;
	}

	private int getPriority(InventoryGroup area) {
		int priority = 0;
		String value = ServiceAreaUtils
		        .getServiceAreaProperty(area, "Priority");
		try {
			priority = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		return priority;
	}

	private long getMinDownloadSpeed(InventoryGroup area) {
		long speed = 0;
		String value = ServiceAreaUtils.getServiceAreaProperty(area,
		        "MinDownloadSpeed");
		try {
			speed = BandwidthUtils.parseSpeed(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		return speed;
	}

	private long getMaxDownloadSpeed(InventoryGroup area) {
		long speed = 0;
		String value = ServiceAreaUtils.getServiceAreaProperty(area,
		        "MaxDownloadSpeed");
		try {
			speed = BandwidthUtils.parseSpeed(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		return speed;
	}

	private long getMinUploadSpeed(InventoryGroup area) {
		long speed = 0;
		String value = ServiceAreaUtils.getServiceAreaProperty(area,
		        "MinUploadSpeed");
		try {
			speed = BandwidthUtils.parseSpeed(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		return speed;
	}

	private long getMaxUploadSpeed(InventoryGroup area) {
		long speed = 0;
		String value = ServiceAreaUtils.getServiceAreaProperty(area,
		        "MaxUploadSpeed");
		try {
			speed = BandwidthUtils.parseSpeed(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		return speed;
	}

}
