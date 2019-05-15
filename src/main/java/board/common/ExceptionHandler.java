package board.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice // 이 클래스는 예외처리 클래스다.
@Slf4j
public class ExceptionHandler {
	
	@org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)	// 메서드에서 처리할 예외
	public ModelAndView defaultExceptionHandler(HttpServletRequest request, Exception exception) {
		ModelAndView mv = new ModelAndView("/error/error_default");	// 예외 발생 시 보여줄 화면
		mv.addObject("exception", exception);
		
		log.error("exception", exception);	// 에러 로그 출력
		
		return mv;
	}
}
