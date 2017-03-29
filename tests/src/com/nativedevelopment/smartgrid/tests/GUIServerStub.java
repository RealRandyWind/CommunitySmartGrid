package com.nativedevelopment.smartgrid.tests;

import com.nativedevelopment.smartgrid.IController;

import java.util.UUID;

public class GUIServerStub extends AServerStub {
	public GUIServerStub(UUID oIdentifier) {
		super(oIdentifier);
	}

	public void SetController(IController oControler) {

	}

	@Override
	public void run() {

	}
}
