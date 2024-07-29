package box.bookstorebe.service;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.util.BeanUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseService {
    private RequestScope requestScopeOverride;
    protected HttpServletRequest request;

    public RequestScope getCurrentUserInfo(){
        try {
            if(this.requestScopeOverride != null) {
                return this.requestScopeOverride;
            }
            return BeanUtils.getBean(RequestScope.class);

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
