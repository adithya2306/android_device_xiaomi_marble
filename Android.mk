#
# Copyright (C) 2021 The Android Open Source Project
#
# SPDX-License-Identifier: Apache-2.0
#

LOCAL_PATH := $(call my-dir)

ifeq ($(TARGET_DEVICE),lisa)

include $(call all-makefiles-under,$(LOCAL_PATH))

include $(CLEAR_VARS)

# Symlinks
EGL_LIBRARIES := \
    libEGL_adreno.so \
    libGLESv2_adreno.so \
    libq3dtools_adreno.so

EGL_32_SYMLINKS := $(addprefix $(TARGET_OUT_VENDOR)/lib/,$(notdir $(EGL_LIBRARIES)))
$(EGL_32_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "EGL 32 lib link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf egl/$(notdir $@) $@

EGL_64_SYMLINKS := $(addprefix $(TARGET_OUT_VENDOR)/lib64/,$(notdir $(EGL_LIBRARIES)))
$(EGL_64_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "EGL lib link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf egl/$(notdir $@) $@

RFS_MSM_ADSP_SYMLINKS := $(TARGET_OUT_VENDOR)/rfs/msm/adsp/
$(RFS_MSM_ADSP_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating RFS MSM ADSP folder structure: $@"
	@rm -rf $@/*
	@mkdir -p $(dir $@)/readonly/vendor
	$(hide) ln -sf /data/vendor/tombstones/rfs/lpass $@/ramdumps
	$(hide) ln -sf /mnt/vendor/persist/rfs/msm/adsp $@/readwrite
	$(hide) ln -sf /mnt/vendor/persist/rfs/shared $@/shared
	$(hide) ln -sf /mnt/vendor/persist/hlos_rfs/shared $@/hlos
	$(hide) ln -sf /vendor/firmware_mnt $@/readonly/firmware
	$(hide) ln -sf /vendor/firmware $@/readonly/vendor/firmware

RFS_MSM_CDSP_SYMLINKS := $(TARGET_OUT_VENDOR)/rfs/msm/cdsp/
$(RFS_MSM_CDSP_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating RFS MSM CDSP folder structure: $@"
	@rm -rf $@/*
	@mkdir -p $(dir $@)/readonly/vendor
	$(hide) ln -sf /data/vendor/tombstones/rfs/cdsp $@/ramdumps
	$(hide) ln -sf /mnt/vendor/persist/rfs/msm/cdsp $@/readwrite
	$(hide) ln -sf /mnt/vendor/persist/rfs/shared $@/shared
	$(hide) ln -sf /mnt/vendor/persist/hlos_rfs/shared $@/hlos
	$(hide) ln -sf /vendor/firmware_mnt $@/readonly/firmware
	$(hide) ln -sf /vendor/firmware $@/readonly/vendor/firmware

RFS_MSM_MPSS_SYMLINKS := $(TARGET_OUT_VENDOR)/rfs/msm/mpss/
$(RFS_MSM_MPSS_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating RFS MSM MPSS folder structure: $@"
	@rm -rf $@/*
	@mkdir -p $(dir $@)/readonly/vendor
	$(hide) ln -sf /data/vendor/tombstones/rfs/modem $@/ramdumps
	$(hide) ln -sf /mnt/vendor/persist/rfs/msm/mpss $@/readwrite
	$(hide) ln -sf /mnt/vendor/persist/rfs/shared $@/shared
	$(hide) ln -sf /mnt/vendor/persist/hlos_rfs/shared $@/hlos
	$(hide) ln -sf /vendor/firmware_mnt $@/readonly/firmware
	$(hide) ln -sf /vendor/firmware $@/readonly/vendor/firmware

RFS_MSM_SLPI_SYMLINKS := $(TARGET_OUT_VENDOR)/rfs/msm/slpi/
$(RFS_MSM_SLPI_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating RFS MSM SLPI folder structure: $@"
	@rm -rf $@/*
	@mkdir -p $(dir $@)/readonly/vendor
	$(hide) ln -sf /data/vendor/tombstones/rfs/slpi $@/ramdumps
	$(hide) ln -sf /mnt/vendor/persist/rfs/msm/slpi $@/readwrite
	$(hide) ln -sf /mnt/vendor/persist/rfs/shared $@/shared
	$(hide) ln -sf /mnt/vendor/persist/hlos_rfs/shared $@/hlos
	$(hide) ln -sf /vendor/firmware_mnt $@/readonly/firmware
	$(hide) ln -sf /vendor/firmware $@/readonly/vendor/firmware

ALL_DEFAULT_INSTALLED_MODULES += \
    $(EGL_32_SYMLINKS) \
    $(EGL_64_SYMLINKS) \
    $(RFS_MSM_ADSP_SYMLINKS) \
    $(RFS_MSM_CDSP_SYMLINKS) \
    $(RFS_MSM_MPSS_SYMLINKS) \
    $(RFS_MSM_SLPI_SYMLINKS)

# WiFi firmware symlinks
WLAN_CHIPSETS := wlan qca6750 qca6390
$(foreach chip, $(WLAN_CHIPSETS), \
	$(shell mkdir -p $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/$(chip); \
	ln -sf /vendor/etc/wifi/$(chip)/WCNSS_qcom_cfg.ini \
	$(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/$(chip)/WCNSS_qcom_cfg.ini; \
	ln -sf /mnt/vendor/persist/wlan/wlan_mac.bin \
	$(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/$(chip)/wlan_mac.bin))

endif
