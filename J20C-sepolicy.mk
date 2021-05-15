ifeq ($(J20C_SEPOLICY_INCLUDED),)

BOARD_SEPOLICY_DIRS += \
    device/xiaomi/J20C-sepolicy/batterysecret/vendor \
    device/xiaomi/J20C-sepolicy/power/vendor \
    device/xiaomi/J20C-sepolicy/camera/vendor \
    device/xiaomi/J20C-sepolicy/ir/vendor \
    device/xiaomi/J20C-sepolicy/fingerprint/vendor \
    device/xiaomi/J20C-sepolicy/leds/vendor \
    device/xiaomi/J20C-sepolicy/qcom-extra/vendor \

J20C_SEPOLICY_INCLUDED := true
endif
