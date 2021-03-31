ifeq ($(SURYA_SEPOLICY_INCLUDED),)

BOARD_SEPOLICY_DIRS += \
    device/xiaomi/surya-sepolicy/batterysecret/vendor \
    device/xiaomi/surya-sepolicy/power/vendor \
    device/xiaomi/surya-sepolicy/camera/vendor \
    device/xiaomi/surya-sepolicy/ir/vendor \
    device/xiaomi/surya-sepolicy/fingerprint/vendor \
    device/xiaomi/surya-sepolicy/leds/vendor \

SURYA_SEPOLICY_INCLUDED := true
endif
