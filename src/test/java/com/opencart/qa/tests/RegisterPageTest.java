package com.opencart.qa.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.opencart.qa.base.BaseTest;
import com.opencart.qa.utils.AppConstants;
import com.opencart.qa.utils.CSVUtil;
import com.opencart.qa.utils.ExcelUtil;

public class RegisterPageTest extends BaseTest{
	
	
	
	@BeforeClass
	public void regSetup() {
		registerPage = loginPage.navigateToRegisterPage();
	}
	
	
	@DataProvider
	public Object[][] getUserRegTestData() {
		return new Object[][] {
			{"gaurav", "sharma", "9878987678", "gaurav@123", "yes"},
			{"anurag", "automation", "9878987687", "anurag@123", "no"},
			{"priya", "automationn", "2378987678", "priya@123", "yes"},
		};
	}
	
	
	@DataProvider
	public Object[][] getUserRegExcelTestData(){
		return ExcelUtil.getTestData(AppConstants.REGISTER_SHEET_NAME);
	}
	
	
	@DataProvider
	public Object[][] getUserRegCSVTestData(){
		return CSVUtil.csvData(AppConstants.REGISTER_SHEET_NAME);
	}
	
	
	@Test(dataProvider = "getUserRegExcelTestData")
	public void userRegisterTest(String firstName, String lastName, String telephone, String password, String subscribe) {
		
		Assert.assertTrue(registerPage.userRegisteration(firstName, lastName, telephone, password, subscribe));
	
	}
	

}