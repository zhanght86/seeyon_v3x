所有的插件定义文件都放在这里

示例

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">

<beans default-autowire="byName">
	<bean id="mobileKingPluginDefintion" class="com.seeyon.v3x.plugin.PluginDefintion">
		<property name="id" value="kingmobile" />
		<property name="name" value="短信王" />
		<property name="contextConfigLocation">
			<list>
				<value>classpath*:mobileKingMessage.xml</value>
			</list>
		</property>
		<property name="properties">
			<list>
				<value>classpath*:kingmobile.properties</value>
			</list>
		</property>
		<property name="hbmResources">
			<list>
				<value> com/seeyon/v3x/plugin/kingmobile/model/phone2PerMapping.hbm.xml </value>
			</list>
		</property>
		<property name="menuI18NResource" value="com.seeyon.v3x.plugin.kingmobile.resouces.i18n.KingMobileResouce" />
		<property name="userMenus">
			<list>
				<bean class="com.seeyon.v3x.plugin.PluginMainMenu">
					<property name="name" value="kingmobile.sendSMS.label" />
					<property name="children">
						<list>
							<bean class="com.seeyon.v3x.plugin.PluginMenu">
								<property name="name" value="kingmobile.send.label" />
								<property name="icon" value="/apps_res/kingmobile/images/send.gif" />
								<property name="url"  value="/kingmobile.do?mehtod=send" />
								<property name="pluginMenuSecurity">
									<bean class="com.seeyon.v3x.plugin.kingmobile.SendSecurity" />
								</property>
							</bean>
							<bean class="com.seeyon.v3x.plugin.PluginMenu">
								<property name="name" value="kingmobile.recieve.label" />
								<property name="icon" value="/apps_res/kingmobile/images/recieve.gif" />
								<property name="url"  value="/kingmobile.do?mehtod=recieve" />
								<property name="pluginMenuSecurity">
									<bean class="com.seeyon.v3x.plugin.kingmobile.RecieveSecurity" />
								</property>
							</bean>
						</list>
					</property>
				</bean>
			</list>
		</property>
		<property name="systemMenus">
			<list>
				<bean class="com.seeyon.v3x.plugin.PluginMainMenu">
					<property name="name" value="kingmobile.set.label" />
					<property name="children">
						<list>
							<bean class="com.seeyon.v3x.plugin.PluginMenu">
								<property name="name" value="kingmobile.senderset.label" />
								<property name="icon" value="/apps_res/kingmobile/images/senderset.gif" />
								<property name="url"  value="/kingmobile.do?mehtod=senderset" />
							</bean>
							<bean class="com.seeyon.v3x.plugin.PluginMenu">
								<property name="name" value="kingmobile.recieveset.label" />
								<property name="icon" value="/apps_res/kingmobile/images/recieveset.gif" />
								<property name="url"  value="/kingmobile.do?mehtod=recieveset" />
							</bean>
						</list>
					</property>
				</bean>
				<bean class="com.seeyon.v3x.plugin.PluginMainMenu">
					<property name="name" value="kingmobile.tongji.label" />
					<property name="children">
						<list>
							<bean class="com.seeyon.v3x.plugin.PluginMenu">
								<property name="name" value="kingmobile.bumentongji.label" />
								<property name="icon" value="/apps_res/kingmobile/images/bumentongji.gif" />
								<property name="url"  value="/kingmobile.do?mehtod=bumentongji" />
							</bean>
							<bean class="com.seeyon.v3x.plugin.PluginMenu">
								<property name="name" value="kingmobile.bumenchaxun.label" />
								<property name="icon" value="/apps_res/kingmobile/images/bumenchaxun.gif" />
								<property name="url"  value="/kingmobile.do?mehtod=bumenchaxun" />
							</bean>
						</list>
					</property>
				</bean>
			</list>
		</property>
	</bean>
</beans>
