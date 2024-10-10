 <?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<Configuration status="WARN" monitorInterval="30">
 
    <!-- Logging Properties FILE LOCATION -->
    <Properties>       
       <Property name="LOG_PATTERN">%-5p [%-15.15t] %d{ISO8601} \: %C{1}.%M \: %m%n</Property>
       <Property name="APP_LOG_ROOT">/ibm/CBS_Portal/logs</Property>   
    </Properties>
     
    
    <Appenders>
     
        <!-- Console Appender -->
        <Console name="Console" target="SYSTEM_OUT" follow="true">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>
         
        <!-- File Appenders on need basis -->
        <!-- <RollingFile name="frameworkLog" fileName="${APP_LOG_ROOT}/app-framework.log"
            filePattern="${APP_LOG_ROOT}/app-framework-%d{yyyy-MM-dd}-%i.log">
            <LevelRangeFilter minLevel="ERROR" maxLevel="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB" />
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile> -->
         
        <!-- <RollingFile name="debugLog" fileName="${APP_LOG_ROOT}/app-debug.log"
            filePattern="${APP_LOG_ROOT}/app-debug-%d{yyyy-MM-dd}-%i.log">
            <LevelRangeFilter minLevel="DEBUG" maxLevel="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB" />
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile> -->
         
        <!-- <RollingFile name="infoLog" fileName="${APP_LOG_ROOT}/app-info.log"
            filePattern="${APP_LOG_ROOT}/app-info-%d{yyyy-MM-dd}-%i.log" >
            <LevelRangeFilter minLevel="INFO" maxLevel="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB" />
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile> -->
         
      <!--   <RollingFile name="errorLog" fileName="${APP_LOG_ROOT}/app-error.log"
            filePattern="${APP_LOG_ROOT}/app-error-%d{yyyy-MM-dd}-%i.log" >
            <LevelRangeFilter minLevel="ERROR" maxLevel="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB" />
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile> -->
         
      <!--   <RollingFile name="perfLog" fileName="${APP_LOG_ROOT}/eForexLogs.log"
            filePattern="${APP_LOG_ROOT}/%d{yyyy-MM-dd}.%i.log.gz" >
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB" />
            </Policies>
            <DefaultRolloverStrategy max="1"/>
        </RollingFile>
		          -->
		
			<RollingFile name="perfLog" fileName="${APP_LOG_ROOT}/cbRep.log"  filePattern="${APP_LOG_ROOT}/cbRep-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
			<RollingFile name="debugLog" fileName="${APP_LOG_ROOT}/cbRep.log"  filePattern="${APP_LOG_ROOT}/cbRep-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
			<RollingFile name="infoLog" fileName="${APP_LOG_ROOT}/cbRep.log" filePattern="${APP_LOG_ROOT}/cbRep-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
			<RollingFile name="errorLog" fileName="${APP_LOG_ROOT}/cbRep.log" filePattern="${APP_LOG_ROOT}/cbRep-%d{dd-MM-yyyy}-%i.log" ignoreExceptions="false">
				<PatternLayout pattern="${LOG_PATTERN}"/>
				<Policies>
					<OnStartupTriggeringPolicy />
					<SizeBasedTriggeringPolicy size="50MB" />
					<TimeBasedTriggeringPolicy />
				</Policies>
				<DefaultRolloverStrategy max="250" />
			</RollingFile>
          
          
          
       <!--  <RollingFile name="traceLog" fileName="${APP_LOG_ROOT}/app-trace.log"
            filePattern="${APP_LOG_ROOT}/app-trace-%d{yyyy-MM-dd}-%i.log" >
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="19500KB" />
            </Policies>
            <DefaultRolloverStrategy max="1"/>
        </RollingFile> -->
         
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
        <!-- <Logger name="org.apache.logging.log4j.Logger;" additivity="false" level="log">
            <AppenderRef ref="perfLog" />
            <AppenderRef ref="Console"/>
        </Logger> -->
                 
        <Root level="info" >
            <AppenderRef ref="Console"/>
              <AppenderRef ref="infoLog"/>
        </Root>
 
    </Loggers>
 
</Configuration>
