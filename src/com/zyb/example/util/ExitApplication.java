package com.zyb.example.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * 退出应用并关闭游戏界面
 * @author Administrator
 *
 */
public class ExitApplication extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	
	private static ExitApplication instance;
	
	private ExitApplication(){
		
	}
	
	//单例模式中获取唯一的ExitApplication 实例
	public static ExitApplication getInstance(){
		if(instance == null){
			instance = new ExitApplication();
		}
		
		return instance;
	}
	
	 //添加Activity 到容器中
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//遍历所有Activity 并finish
	
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		
		System.exit(0);
	}
}
