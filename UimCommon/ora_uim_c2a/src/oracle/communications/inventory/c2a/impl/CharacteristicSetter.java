package oracle.communications.inventory.c2a.impl;
/*
REPLACE_COPYRIGHT_HERE
*/
import java.util.Set;

import oracle.communications.inventory.api.TimeBound;
import oracle.communications.inventory.api.entity.CharacteristicSpecification;
import oracle.communications.inventory.api.entity.ControlType;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.CharValue;
import oracle.communications.inventory.api.entity.common.CharacteristicExtensible;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationItem;
import oracle.communications.inventory.api.entity.common.RootEntity;

public class CharacteristicSetter {
	private CharacteristicExtensible target;
	private String name;

	public CharacteristicSetter(CharacteristicExtensible target, String name) {
		this.target = target;
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		Set<CharValue> chars = this.target.getCharacteristics();
		CharValue thisChar = null;
		for (CharValue c : chars) {
			if (this.name.equals(c.getName())) {
				thisChar = c;
				break;
			}
		}
		if (thisChar == null) {
			Specification spec = null;
			if (this.target instanceof RootEntity) {
				RootEntity entity = (RootEntity) this.target;
				spec = entity.getSpecification();
			} else if (this.target instanceof InventoryConfigurationItem) {
				InventoryConfigurationItem configItem = (InventoryConfigurationItem) this.target;
				spec = configItem.getConfigSpec();
			}
			if (spec == null) {
				return;
			}
			CharacteristicSpecification cs = EntityUtils
			        .getCharacteristicSpecification(spec, this.name);
			if (cs == null) {
				return;
			}
			thisChar = this.target.makeCharacteristicInstance();
			thisChar.setCharacteristicSpecification(cs);
			thisChar.setName(cs.getName());
			if (this.target instanceof TimeBound) {
				thisChar.setValidFor(((TimeBound) this.target).getValidFor());
			}
			if (cs.getEntityLinkClass() != null && value == null) {
				// not allowed to set reference to null on a characteristic
				return;
			}
			chars.add(thisChar);
			this.target.setCharacteristics(chars);
		}
		if (value instanceof RootEntity) {
			RootEntity entity = (RootEntity) value;
			thisChar.setValue(entity.getName());
		} else if (ControlType.CHECKBOX.equals(thisChar
		        .getCharacteristicSpecification().getControlType())) {
			String booleanValue = "true".equals(value.toString()) ? "true"
			        : "false";
			thisChar.setValue(booleanValue);
		} else {
			thisChar.setValue(value.toString());
		}
		// leave the label unset - CharacteristicManager.validateDropdownList
		// will set it later.
	}
}
