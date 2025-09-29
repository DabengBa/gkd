package li.songe.gkd.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import li.songe.gkd.appScope
import li.songe.gkd.util.AppListString
import li.songe.gkd.util.launchTry
import li.songe.gkd.util.toast

val storeFlow: MutableStateFlow<SettingsStore> by lazy {
    createAnyFlow(
        key = "store",
        default = { SettingsStore() }
    )
}

val actionCountFlow: MutableStateFlow<Long> by lazy {
    createTextFlow(
        key = "action_count",
        decode = { it?.toLongOrNull() ?: 0L },
        encode = { it.toString() },
    )
}

val blockMatchAppListFlow: MutableStateFlow<Set<String>> by lazy {
    createTextFlow(
        key = "block_match_app_list",
        decode = { it?.let(AppListString::decode) ?: AppListString.getDefaultBlockList() },
        encode = AppListString::encode,
    )
}

val blockA11yAppListFlow: MutableStateFlow<Set<String>> by lazy {
    createTextFlow(
        key = "block_a11y_app_list",
        decode = { it?.let(AppListString::decode) ?: emptySet() },
        encode = AppListString::encode,
    )
}

val actualBlockA11yAppList: Set<String>
    get() = if (storeFlow.value.blockA11yAppListFollowMatch) {
        blockMatchAppListFlow.value
    } else {
        blockA11yAppListFlow.value
    }

fun checkAppBlockMatch(appId: String): Boolean {
    if (blockMatchAppListFlow.value.contains(appId)) {
        return true
    }
    if (storeFlow.value.enableBlockA11yAppList) {
        return actualBlockA11yAppList.contains(appId)
    }
    return false
}

fun initStore() = appScope.launchTry(Dispatchers.IO) {
    // preload
    storeFlow.value
    actionCountFlow.value
    blockMatchAppListFlow.value
    blockA11yAppListFlow.value
}

fun switchStoreEnableMatch() {
    if (storeFlow.value.enableMatch) {
        toast("暂停规则匹配")
    } else {
        toast("开启规则匹配")
    }
    storeFlow.update { it.copy(enableMatch = !it.enableMatch) }
}
