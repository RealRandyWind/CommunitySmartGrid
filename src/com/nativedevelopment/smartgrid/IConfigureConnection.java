package com.nativedevelopment.smartgrid;

import java.io.Serializable;
import java.util.UUID;

public interface IConfigureConnection extends Serializable {
	public UUID GetIdentifier();
	public ISetting[] GetSettings();
}
