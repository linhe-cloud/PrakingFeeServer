package com.charge.service;

import com.charge.entity.ChargeRule;
import com.charge.entity.Member;
import com.charge.entity.DiscountRule;
import com.charge.entity.VO.CalculateFeeResponse;
import com.parking.entity.ParkingIot;
import com.common.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 计费缓存服务
 * 统一管理计费模块的所有缓存逻辑
 */
@Slf4j
@Service
public class ChargeCacheService {

    private final RedisService redisService;

    // ========== 缓存Key前缀 ==========
    private static final String CHARGE_RULE_PREFIX = "charge:rule:parkingIotId:";
    private static final String PARKING_IOT_PREFIX = "charge:parking:iot:";
    private static final String MEMBER_PREFIX = "charge:member:userId:";
    private static final String DISCOUNT_RULE_PREFIX = "charge:discount:rule:";
    private static final String FEE_PREVIEW_PREFIX = "charge:preview:";

    // ========== 缓存过期时间（秒） ==========
    private static final long CHARGE_RULE_EXPIRE = 1800L;      // 30分钟
    private static final long PARKING_IOT_EXPIRE = 3600L;      // 1小时
    private static final long MEMBER_EXPIRE = 600L;            // 10分钟
    private static final long DISCOUNT_RULE_EXPIRE = 1800L;    // 30分钟
    private static final long FEE_PREVIEW_EXPIRE = 300L;       // 5分钟

    public ChargeCacheService(RedisService redisService) {
        this.redisService = redisService;
    }

    // ==================== 计费规则缓存 ====================

    /**
     * 缓存计费规则
     *
     * @param parkingIotId 车场ID
     * @param rule 计费规则（可为null，表示无规则）
     */
    public void cacheChargeRule(Long parkingIotId, ChargeRule rule) {
        try {
            String key = CHARGE_RULE_PREFIX + parkingIotId;
            if (rule == null) {
                // 缓存一个特殊值表示"无规则"，避免缓存穿透
                redisService.set(key, "NULL", CHARGE_RULE_EXPIRE);
            } else {
                redisService.set(key, rule, CHARGE_RULE_EXPIRE);
            }
            log.debug("缓存计费规则成功: parkingIotId={}, ruleId={}", parkingIotId, 
                rule != null ? rule.getId() : null);
        } catch (Exception e) {
            log.error("缓存计费规则失败: parkingIotId={}", parkingIotId, e);
        }
    }

    /**
     * 获取计费规则（从缓存）
     *
     * @param parkingIotId 车场ID
     * @return 计费规则，如果缓存不存在返回null
     */
    public ChargeRule getChargeRule(Long parkingIotId) {
        try {
            String key = CHARGE_RULE_PREFIX + parkingIotId;
            Object obj = redisService.get(key);
            if (obj == null) {
                return null;
            }
            if ("NULL".equals(obj)) {
                // 表示数据库中无此规则
                return new ChargeRule(); // 返回空对象作为标记
            }
            return (ChargeRule) obj;
        } catch (Exception e) {
            log.error("获取计费规则缓存失败: parkingIotId={}", parkingIotId, e);
            return null;
        }
    }

    /**
     * 删除计费规则缓存
     *
     * @param parkingIotId 车场ID
     */
    public void evictChargeRule(Long parkingIotId) {
        try {
            String key = CHARGE_RULE_PREFIX + parkingIotId;
            redisService.del(key);
            log.debug("删除计费规则缓存成功: parkingIotId={}", parkingIotId);
        } catch (Exception e) {
            log.error("删除计费规则缓存失败: parkingIotId={}", parkingIotId, e);
        }
    }

    // ==================== 车场信息缓存 ====================

    /**
     * 缓存车场信息
     *
     * @param parkingIot 车场信息
     */
    public void cacheParkingIot(ParkingIot parkingIot) {
        try {
            if (parkingIot == null || parkingIot.getId() == null) {
                return;
            }
            String key = PARKING_IOT_PREFIX + parkingIot.getId();
            redisService.set(key, parkingIot, PARKING_IOT_EXPIRE);
            log.debug("缓存车场信息成功: parkingIotId={}", parkingIot.getId());
        } catch (Exception e) {
            log.error("缓存车场信息失败: parkingIotId={}", 
                parkingIot != null ? parkingIot.getId() : null, e);
        }
    }

    /**
     * 获取车场信息（从缓存）
     *
     * @param parkingIotId 车场ID
     * @return 车场信息，如果缓存不存在返回null
     */
    public ParkingIot getParkingIot(Long parkingIotId) {
        try {
            String key = PARKING_IOT_PREFIX + parkingIotId;
            Object obj = redisService.get(key);
            return obj != null ? (ParkingIot) obj : null;
        } catch (Exception e) {
            log.error("获取车场信息缓存失败: parkingIotId={}", parkingIotId, e);
            return null;
        }
    }

    /**
     * 删除车场信息缓存
     *
     * @param parkingIotId 车场ID
     */
    public void evictParkingIot(Long parkingIotId) {
        try {
            String key = PARKING_IOT_PREFIX + parkingIotId;
            redisService.del(key);
            log.debug("删除车场信息缓存成功: parkingIotId={}", parkingIotId);
        } catch (Exception e) {
            log.error("删除车场信息缓存失败: parkingIotId={}", parkingIotId, e);
        }
    }

    // ==================== 会员信息缓存 ====================

    /**
     * 缓存会员信息
     *
     * @param userId 用户ID
     * @param member 会员信息（可为null，表示非会员）
     */
    public void cacheMember(Long userId, Member member) {
        try {
            String key = MEMBER_PREFIX + userId;
            if (member == null) {
                // 缓存特殊值表示"非会员"，避免缓存穿透
                redisService.set(key, "NULL", MEMBER_EXPIRE);
            } else {
                redisService.set(key, member, MEMBER_EXPIRE);
            }
            log.debug("缓存会员信息成功: userId={}, memberId={}", userId, 
                member != null ? member.getId() : null);
        } catch (Exception e) {
            log.error("缓存会员信息失败: userId={}", userId, e);
        }
    }

    /**
     * 获取会员信息（从缓存）
     *
     * @param userId 用户ID
     * @return 会员信息，如果缓存不存在返回null
     */
    public Member getMember(Long userId) {
        try {
            String key = MEMBER_PREFIX + userId;
            Object obj = redisService.get(key);
            if (obj == null) {
                return null;
            }
            if ("NULL".equals(obj)) {
                // 表示非会员
                return new Member(); // 返回空对象作为标记
            }
            return (Member) obj;
        } catch (Exception e) {
            log.error("获取会员信息缓存失败: userId={}", userId, e);
            return null;
        }
    }

    /**
     * 删除会员信息缓存
     *
     * @param userId 用户ID
     */
    public void evictMember(Long userId) {
        try {
            String key = MEMBER_PREFIX + userId;
            redisService.del(key);
            log.debug("删除会员信息缓存成功: userId={}", userId);
        } catch (Exception e) {
            log.error("删除会员信息缓存失败: userId={}", userId, e);
        }
    }

    // ==================== 优惠规则缓存 ====================

    /**
     * 缓存优惠规则
     *
     * @param ruleCode 规则编码
     * @param rule 优惠规则（可为null，表示无此规则）
     */
    public void cacheDiscountRule(String ruleCode, DiscountRule rule) {
        try {
            if (ruleCode == null || ruleCode.isEmpty()) {
                return;
            }
            String key = DISCOUNT_RULE_PREFIX + ruleCode;
            if (rule == null) {
                // 缓存特殊值表示"无此规则"，避免缓存穿透
                redisService.set(key, "NULL", DISCOUNT_RULE_EXPIRE);
            } else {
                redisService.set(key, rule, DISCOUNT_RULE_EXPIRE);
            }
            log.debug("缓存优惠规则成功: ruleCode={}, ruleId={}", ruleCode, 
                rule != null ? rule.getId() : null);
        } catch (Exception e) {
            log.error("缓存优惠规则失败: ruleCode={}", ruleCode, e);
        }
    }

    /**
     * 获取优惠规则（从缓存）
     *
     * @param ruleCode 规则编码
     * @return 优惠规则，如果缓存不存在返回null
     */
    public DiscountRule getDiscountRule(String ruleCode) {
        try {
            if (ruleCode == null || ruleCode.isEmpty()) {
                return null;
            }
            String key = DISCOUNT_RULE_PREFIX + ruleCode;
            Object obj = redisService.get(key);
            if (obj == null) {
                return null;
            }
            if ("NULL".equals(obj)) {
                // 表示无此规则
                return new DiscountRule(); // 返回空对象作为标记
            }
            return (DiscountRule) obj;
        } catch (Exception e) {
            log.error("获取优惠规则缓存失败: ruleCode={}", ruleCode, e);
            return null;
        }
    }

    /**
     * 删除优惠规则缓存
     *
     * @param ruleCode 规则编码
     */
    public void evictDiscountRule(String ruleCode) {
        try {
            if (ruleCode == null || ruleCode.isEmpty()) {
                return;
            }
            String key = DISCOUNT_RULE_PREFIX + ruleCode;
            redisService.del(key);
            log.debug("删除优惠规则缓存成功: ruleCode={}", ruleCode);
        } catch (Exception e) {
            log.error("删除优惠规则缓存失败: ruleCode={}", ruleCode, e);
        }
    }

    // ==================== 费用预览缓存（可选） ====================

    /**
     * 缓存费用预览结果
     *
     * @param inRecordId 入场记录ID
     * @param outTime 出场时间
     * @param response 预览结果
     */
    public void cacheFeePreview(Long inRecordId, LocalDateTime outTime, 
                                CalculateFeeResponse response) {
        try {
            String timeStr = outTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String key = FEE_PREVIEW_PREFIX + inRecordId + ":" + timeStr;
            redisService.set(key, response, FEE_PREVIEW_EXPIRE);
            log.debug("缓存费用预览成功: inRecordId={}, outTime={}", inRecordId, timeStr);
        } catch (Exception e) {
            log.error("缓存费用预览失败: inRecordId={}", inRecordId, e);
        }
    }

    /**
     * 获取费用预览结果（从缓存）
     *
     * @param inRecordId 入场记录ID
     * @param outTime 出场时间
     * @return 预览结果，如果缓存不存在返回null
     */
    public CalculateFeeResponse getFeePreview(Long inRecordId, LocalDateTime outTime) {
        try {
            String timeStr = outTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String key = FEE_PREVIEW_PREFIX + inRecordId + ":" + timeStr;
            Object obj = redisService.get(key);
            return obj != null ? (CalculateFeeResponse) obj : null;
        } catch (Exception e) {
            log.error("获取费用预览缓存失败: inRecordId={}", inRecordId, e);
            return null;
        }
    }

    /**
     * 删除费用预览缓存
     *
     * @param inRecordId 入场记录ID
     */
    public void evictFeePreview(Long inRecordId) {
        try {
            // 使用Lua脚本删除所有匹配的预览缓存
            String pattern = FEE_PREVIEW_PREFIX + inRecordId + ":*";
            String luaScript = 
                "local keys = redis.call('keys', ARGV[1]) " +
                "if #keys > 0 then " +
                "    return redis.call('del', unpack(keys)) " +
                "else " +
                "    return 0 " +
                "end";
            
            redisService.executeLuaScript(luaScript, Long.class, 
                java.util.Collections.emptyList(), pattern);
            log.debug("删除费用预览缓存成功: inRecordId={}", inRecordId);
        } catch (Exception e) {
            log.error("删除费用预览缓存失败: inRecordId={}", inRecordId, e);
        }
    }

    // ==================== 批量清除缓存 ====================

    /**
     * 清除所有计费相关缓存（谨慎使用）
     */
    public void evictAllChargeCache() {
        try {
            String pattern = "charge:*";
            String luaScript = 
                "local keys = redis.call('keys', ARGV[1]) " +
                "if #keys > 0 then " +
                "    return redis.call('del', unpack(keys)) " +
                "else " +
                "    return 0 " +
                "end";
            
            redisService.executeLuaScript(luaScript, Long.class, 
                java.util.Collections.emptyList(), pattern);
            log.info("清除所有计费缓存成功");
        } catch (Exception e) {
            log.error("清除所有计费缓存失败", e);
        }
    }
}
