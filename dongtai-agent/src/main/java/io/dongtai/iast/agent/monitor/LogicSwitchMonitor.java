package io.dongtai.iast.agent.monitor;

import io.dongtai.iast.agent.manager.EngineManager;
import io.dongtai.log.DongTaiLog;

/**
 * 二次降级开关(大流量熔断器、性能熔断器、异常熔断器)监控器
 *
 * @author liyuan40
 * @date 2022/3/7 20:03
 */
public class LogicSwitchMonitor implements IMonitor {

    private final EngineManager engineManager;

    public LogicSwitchMonitor(EngineManager engineManager) {
        this.engineManager = engineManager;
    }

    @Override
    public void check() {
        Boolean isNeedTurnOffEngine = checkLogicSwitcher();
        if (isNeedTurnOffEngine == null) {
            return;
        }

        if (isNeedTurnOffEngine) {
            engineManager.uninstall();
        }
    }

    /**
     * 检查二次校验熔断器判断结果是否需要关闭引擎
     *
     * @return boolean
     */
    private Boolean checkLogicSwitcher() {
        try {
            final Class<?> limitFallbackSwitch = EngineManager.getLimitFallbackSwitch();
            if (limitFallbackSwitch == null) {
                return null;
            }
            return (Boolean) limitFallbackSwitch.getMethod("isNeedTurnOffEngine").invoke(null);
        } catch (Throwable t) {
            DongTaiLog.error("checkLogicSwitcher failed, msg:{}, err:{}", t.getMessage(), t.getCause());
            return false;
        }
    }
}
