package com.zane.flashsale.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class FlowController {

    @ResponseBody
    @RequestMapping("hello")
    public String hello() {
        String result;
        // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
        // SphU.entry will return BlockException when it detects rule has been broken.
        try (Entry entry = SphU.entry("HelloResource")){
            // 被保护的业务逻辑
            result = "hello test sentinel";
            return result;
        }catch (BlockException ex) {
            // 被降级的代码块处理。
            // 资源访问阻止，被限流或被降级
            // 在此处进行相应的处理操作
            log.error(ex.toString());
            result = "system busy please retry later on";
            return result;
        }
    }

    /**
     * 定义限流规则
     * 1.创建存放限流规则的集合
     * 2.创建限流规则
     * 3.将限流规则放到集合中
     * 4.加载限流规则
     * @PostConstruct will run after construction of current class
     */
    @PostConstruct
    public void flashSaleFlow() {
        // rule list
        List<FlowRule> rules = new ArrayList<>();

        // a rule about HelloResource
        FlowRule rule = new FlowRule();
        rule.setResource("HelloResource"); // set resource name, consistent with try{}catch{}
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS); // set flow rule type : QPS
        rule.setCount(2); // QPS each sec

        FlowRule rule_flashSale = new FlowRule();
        rule_flashSale.setResource("flashSalesResource");
        rule_flashSale.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule_flashSale.setCount(2);

        FlowRule rule_flashSale_item = new FlowRule();
        rule_flashSale.setResource("FlashSaleItemResource");
        rule_flashSale.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule_flashSale.setCount(2);

        // load rules
        rules.add(rule);
        rules.add(rule_flashSale);
        rules.add(rule_flashSale_item);
        FlowRuleManager.loadRules(rules);
    }
}
