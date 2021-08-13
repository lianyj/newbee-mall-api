package ltd.newbee.mall.api.admin;

import ltd.newbee.mall.api.mall.vo.OrderDetailVO;
import ltd.newbee.mall.config.annotation.TokenToAdminUser;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName FreemarkController
 * Description
 * Create by yanjie14
 * Date 2021/8/12 7:34 下午
 */
@Controller
public class FreemarkController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/order/print/{orderId}/{name}")
    public String print(Model model,@PathVariable("orderId") Long orderId ,@PathVariable("name") String name) {
        OrderDetailVO orderDetailVO =  orderService.getOrderDetailByOrderId(orderId,null);
        orderDetailVO.setPrintName(name);
        model.addAttribute(orderDetailVO);
        return "print";
    }
}
