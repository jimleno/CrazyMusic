package com.zyb.example.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * �˳�Ӧ�ò��ر���Ϸ����
 * @author Administrator
 *
 */
public class ExitApplication extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	
	private static ExitApplication instance;
	
	private ExitApplication(){
		
	}
	
	//����ģʽ�л�ȡΨһ��ExitApplication ʵ��
	public static ExitApplication getInstance(){
		if(instance == null){
			instance = new ExitApplication();
		}
		
		return instance;
	}
	
	 //���Activity ��������
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//��������Activity ��finish
	
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		
		System.exit(0);
	}
}
