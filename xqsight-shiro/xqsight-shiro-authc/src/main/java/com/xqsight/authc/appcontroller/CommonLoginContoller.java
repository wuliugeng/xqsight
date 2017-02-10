package com.xqsight.authc.appcontroller;

import com.xqsight.sso.authc.SSOUsernamePasswordToken;
import com.xqsight.sso.enums.UserType;
import com.xqsight.sso.subject.SSOSubject;
import com.xqsight.sso.utils.SSOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @version ShiroContoller.java, v 0.1 2015年7月3日 上午9:49:26
 */
@Controller
@RequestMapping("/login")
public class CommonLoginContoller extends AbstractLoginContoller {

	@RequestMapping("/login")
	protected RedirectView login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		SSOSubject currentUser = SSOUtils.getCurrentUserSubject();
		logger.info("we take u are: name={}, isAuthc={}, isRememberMe={}, isRunAs={}", currentUser.getPrincipal(), currentUser.isAuthenticated(), currentUser.isRemembered(), currentUser.isRunAs());

		if (!currentUser.isAuthenticated()) {
			currentUser.login(getUsernamePasswrdToken(request));
		}
		return getRedirectView(request);
	}

	/**
	 *
	 */
	@Override
	protected SSOUsernamePasswordToken chooseTokenInstance(String username, String password, boolean isRememberMe) {
		return new SSOUsernamePasswordToken(username, password, isRememberMe, UserType.SYSTEM);
	}

}
