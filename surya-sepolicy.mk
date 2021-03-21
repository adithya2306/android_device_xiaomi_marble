ifeq ($(SURYA_SEPOLICY_INCLUDED),)

BOARD_SEPOLICY_DIRS += \
    device/xiaomi/surya-sepolicy/batterysecret/vendor \
    device/xiaomi/surya-sepolicy/power/vendor

SURYA_SEPOLICY_INCLUDED := true
endif
