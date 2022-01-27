ifeq ($(K9_SEPOLICY_INCLUDED),)

BOARD_VENDOR_SEPOLICY_DIRS += \
    device/xiaomi/K9-sepolicy/audio/vendor \
    device/xiaomi/K9-sepolicy/batterysecret/vendor \
    device/xiaomi/K9-sepolicy/power/vendor \
    device/xiaomi/K9-sepolicy/camera/vendor \
    device/xiaomi/K9-sepolicy/ir/vendor \
    device/xiaomi/K9-sepolicy/fingerprint/vendor \
    device/xiaomi/K9-sepolicy/qcom-extra/vendor \
    device/xiaomi/K9-sepolicy/thermal/vendor \
    device/xiaomi/K9-sepolicy/sensors/vendor \
    device/xiaomi/K9-sepolicy/display/vendor \
    device/xiaomi/K9-sepolicy/nfc/vendor

K9_SEPOLICY_INCLUDED := true
endif
