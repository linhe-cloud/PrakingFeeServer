package com.charge.controller.admin;

import com.charge.entity.OrderModificationRecord;
import com.charge.service.OrderModificationService;
import com.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单修改管理接口（管理端）
 */
@RestController
@RequestMapping("/api/admin/order/modify")
public class OrderModificationController {

    private final OrderModificationService modificationService;

    public OrderModificationController(OrderModificationService modificationService) {
        this.modificationService = modificationService;
    }

    /**
     * 申请订单改价
     */
    @PostMapping("/applyAdjust")
    public Result<OrderModificationRecord> applyAdjust(
            @RequestParam("orderId") Long orderId,
            @RequestParam("afterAmount") Long afterAmount,
            @RequestParam("reason") String reason,
            @RequestParam("applyUserId") Long applyUserId,
            @RequestParam("applyUserName") String applyUserName) {

        OrderModificationRecord record = modificationService.applyAmountAdjust(
                orderId, afterAmount, reason, applyUserId, applyUserName
        );
        return Result.success(record);
    }

    /**
     * 申请订单退款
     */
    @PostMapping("/applyRefund")
    public Result<OrderModificationRecord> applyRefund(
            @RequestParam("orderId") Long orderId,
            @RequestParam("reason") String reason,
            @RequestParam("applyUserId") Long applyUserId,
            @RequestParam("applyUserName") String applyUserName) {

        OrderModificationRecord record = modificationService.applyRefund(
                orderId, reason, applyUserId, applyUserName
        );
        return Result.success(record);
    }

    /**
     * 审批通过（主管操作）
     */
    @PostMapping("/approve")
    public Result<Void> approve(
            @RequestParam("recordId") Long recordId,
            @RequestParam("approveUserId") Long approveUserId,
            @RequestParam("approveUserName") String approveUserName,
            @RequestParam(value = "approveRemark", required = false) String approveRemark) {

        modificationService.approve(recordId, approveUserId, approveUserName, approveRemark);
        return Result.success();
    }

    /**
     * 审批拒绝
     */
    @PostMapping("/reject")
    public Result<Void> reject(
            @RequestParam("recordId") Long recordId,
            @RequestParam("approveUserId") Long approveUserId,
            @RequestParam("approveUserName") String approveUserName,
            @RequestParam(value = "approveRemark", required = false) String approveRemark) {

        modificationService.reject(recordId, approveUserId, approveUserName, approveRemark);
        return Result.success();
    }

    /**
     * 查询待审批列表
     */
    @GetMapping("/pendingList")
    public Result<List<OrderModificationRecord>> getPendingList() {
        List<OrderModificationRecord> list = modificationService.getPendingList();
        return Result.success(list);
    }

    /**
     * 按订单查询修改记录
     */
    @GetMapping("/list/{orderId}")
    public Result<List<OrderModificationRecord>> getByOrderId(@PathVariable("orderId") Long orderId) {
        List<OrderModificationRecord> list = modificationService.getByOrderId(orderId);
        return Result.success(list);
    }
}
