package com.societecooperativegroupements.core.models;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Alkemics Asset importer", description = "Societe Cooperative Groupements d Achats des Centres Leclerc Asset importer configuration.")
public @interface AlkemicsAssetImporterConfiguration {

    @AttributeDefinition(name = "Frequency(Cron-job expression)")
    String scheduler_expression() default "0 0 12 1/1 * ? *";

    @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
    boolean scheduler_concurrent() default false;

    @AttributeDefinition(name = "alkemicsProductUrl", description = "Can be configured in /system/console/configMgr")
    String alkemicsProductUrl() default "https://apis.alkemics.com/public/v1/products";

    @AttributeDefinition(name = "alkemicsTokenUrl", description = "Can be configured in /system/console/configMgr")
    String alkemicsTokenUrl() default "https://apis.alkemics.com/auth/v2/token";

    @AttributeDefinition(name = "Client ID", description = "Can be configured in /system/console/configMgr")
    String clientId() default "3708d601d2806caa9045c1881f414791662440ef";

    @AttributeDefinition(name = "Client Secret", description = "Can be configured in /system/console/configMgr")
    String clientSecret() default "3541b4816f92fb5f79d518e10476f4de5b04cfde";

    @AttributeDefinition(name = "Initialisation", description = "Can be configured in /system/console/configMgr")
    boolean init() default false;

    @AttributeDefinition(name = "dryRun", description = "Can be configured in /system/console/configMgr")
    boolean dryRun() default false;

    @AttributeDefinition(name = "Batch Size", description = "Can be configured in /system/console/configMgr")
    int batchSize() default 1000;

    @AttributeDefinition(name = "WaitTime", description = "Can be configured in /system/console/configMgr")
    int waitTime() default 120000;

    @AttributeDefinition(name = "endDateEntry", description = "Can be configured in /system/console/configMgr")
    String endDateEntry() default "01-11-2022";

    @AttributeDefinition(name = "startDateEntry", description = "Can be configured in /system/console/configMgr")
    String startDateEntry() default "28-06-2019";

}