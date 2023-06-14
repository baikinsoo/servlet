package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
// URI 요청이 v3 하위 경로로 오면 아래 메서드가 무조건 실행된다.
public class FrontControllerServletV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();
    // String과 ControllerV3를 참조형으로 하는 controllerMap이라는 HashMap을 생성한다.

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }
    // 해당 메서드를 통해 Map에 URI, new를 통해 객체가 생성된다.

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV3.service");

        String requestURI = request.getRequestURI();
        // 브라우저에 입력된 URI를 받을 수 있다. 회원 가입을 누르게 되면 new-form의 URI가 requestURI에 저장된다.

        ControllerV3 controller = controllerMap.get(requestURI);
        // Map에 저장된 해당 URI에 대한 value 값을 controller 변수에 넣는다. // 여기서 ControllerV3의 참조형이지만 실제로는 MemberFormControllerV3의 객체를 사용한다.
        if (controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 해당 URI에 대한 value가 없으면 404(SC_NOT_FOUND)를 호출하고 아무것도 반환하지 않는다.

        //paramMap
        Map<String, String> paramMap = createParamMap(request);
        // 매배변수를 request로 하고, request 저장소에 있는 key, value 값을 Map<String, String> 형태로 반환한다.
        // 즉, 회원가입 form을 통해 저장된 파라미터의 key, value값을 반환하는 것이다.
        ModelView mv = controller.process(paramMap);
        // createParamMap을 통해 저장된 파라미터 key, value들을 ModelView를 참조형으로 하는 참조변수 mv에 저장한다.
        // 현재 MemberFormControllerV3의 객체를 사용하고 있다.
        // 현재 위의 코드는 ModelView mv = new ModelView("new-form"); 와 동일하다.


        //new-form
        String viewName = mv.getViewName();//논리 이름 new-form 이것밖에 못 가져온다.
        // /WEB-INF ~~~ 로 만들어진다.
        // 매개변수를 포함한 생성자를 통해 해당 viewName이 new-form으로 전달된다.
        MyView view = viewResolver(viewName);
        //viewResolver라는 메서드를 통해 "/WEB-INF/views/new-form.jsp"의 값이 viewPath로 저장된다.

        view.render(mv.getModel(), request, response);
    }

    private static MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    private static Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                        .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
