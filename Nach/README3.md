<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<Configuration status="WARN" monitorInterval="30">
 
    <!-- Logging Properties FILE LOCATION -->
    <Properties>       
       <Property name="LOG_PATTERN">%-5p ACH_RETURN_CR %d{ISO8601} \: %C{1}.%M \: %m%n</Property>
       <Property name="APP_LOG_ROOT">E:\John\John_Workspace ide\ACH_NACH\ACH_CR\src\LOGS\ACH_CR_RETURN.log</Property>   
    </Properties>
     
    
    <Appenders>
     
        <!-- Console Appender -->
        <Console name="Console" target="SYSTEM_OUT" follow="true">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>
         
        <!-- File Appenders on need basis -->
 
		          -->
		
			<RollingFile name="perfLog" fileName="${APP_LOG_ROOT}/ACH_CR_RETURN.log"  filePattern="${APP_LOG_ROOT}/ACH_CR_RETURN-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
			<RollingFile name="debugLog" fileName="${APP_LOG_ROOT}/ACH_CR_RETURN.log"  filePattern="${APP_LOG_ROOT}/ACH_CR_RETURN-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
			<RollingFile name="infoLog" fileName="${APP_LOG_ROOT}/ACH_CR_RETURN.log" filePattern="${APP_LOG_ROOT}/ACH_CR_RETURN-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
			<RollingFile name="errorLog" fileName="${APP_LOG_ROOT}/ACH_CR_RETURN.log" filePattern="${APP_LOG_ROOT}/ACH_CR_RETURN-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
          
    </Appenders>
 
    <Loggers>
     
        <Logger name="org.apache.logging.log4j.Logger;" additivity="false" level="warn">
            <AppenderRef ref="traceLog" />
            <AppenderRef ref="Console" />
        </Logger>
         
        <Logger name="org.apache.logging.log4j.Logger;" additivity="false" level="debug">
            <AppenderRef ref="debugLog" />
            <AppenderRef ref="infoLog"  />
            <AppenderRef ref="errorLog" />
            <AppenderRef ref="Console"  />
        </Logger>
         
        <Logger name="org.apache.logging.log4j.Logger;" additivity="false" level="info">
            <AppenderRef ref="perfLog" />
            <AppenderRef ref="Console"/>
        </Logger>
        
        <Logger name="org.apache.logging.log4j.Logger;" additivity="false" level="info">
            <AppenderRef ref="perfLog" />
            <AppenderRef ref="Console"/>
        </Logger>
        
        <Logger name="org.apache.logging.log4j.Logger;" additivity="false" level="error">
            <AppenderRef ref="perfLog" />
            <AppenderRef ref="Console"/>
        </Logger>
       
                 
        <Root level="info" >
            <AppenderRef ref="Console"/>
              <AppenderRef ref="infoLog"/>
        </Root>
 
    </Loggers>
 
</Configuration>


<!--  <?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n"/>
        </Console>
        <File name="FileLogger" fileName="E:\John\John_Workspace ide\ACH_NACH\ACH_CR\src\LOGS\app.log" append="false">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="DEBUG">  Use a valid log level here 
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileLogger"/>
        </Root>
    </Loggers>
</Configuration>
-->
