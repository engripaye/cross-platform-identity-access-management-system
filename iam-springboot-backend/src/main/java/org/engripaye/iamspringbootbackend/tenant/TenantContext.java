package org.engripaye.iamspringbootbackend.tenant;

public class TenantContext {
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();
    public static void set(String tenantId){
        TENANT.set(tenantId);
    }
    public static String get(){
        return TENANT.get();
    }

    public static void clear() {
        TENANT.remove();
    }
}
