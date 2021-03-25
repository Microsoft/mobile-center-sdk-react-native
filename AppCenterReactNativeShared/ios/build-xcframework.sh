#!/bin/bash

# Build initial xcframework under Xcode 11.3

# Sets the target folders and the final framework product.
export FMK_NAME=AppCenterReactNativeShared
IOS_PLATFORMS="iphoneos iphonesimulator maccatalyst"

# Install dir will be the final output to the framework.
# The following line create it in the root folder of the current project.
PRODUCTS_DIR=${SRCROOT}/../Products
ZIP_FOLDER=${FMK_NAME}
export TEMP_DIR=${PRODUCTS_DIR}/${ZIP_FOLDER}

# Directory of final xcframework
INSTALL_DIR_XCFRAMEWORK=${TEMP_DIR}/${FMK_NAME}.xcframework

# Working dir will be deleted after the framework creation.
export DERIVED_DATA_PATH=build
export WRK_DIR=${DERIVED_DATA_PATH}/Build/Products

# # Cleaning the oldest.
if [ -d "${TEMP_DIR}" ]; then
rm -rf "${TEMP_DIR}"
fi

for sdk in $IOS_PLATFORMS; do
    export INSTALL_DIR=${TEMP_DIR}/Release-${sdk}
    export FMK_DIR=${INSTALL_DIR}/${FMK_NAME}.framework

    #call subscript
    $SRCROOT/build-framework.sh $sdk

    frameworks+=( -framework "${FMK_DIR}")
done

# Building final xcframework
xcodebuild -create-xcframework "${frameworks[@]}" -output "${INSTALL_DIR_XCFRAMEWORK}"

# Copies the license file to the products directory (required for cocoapods)
cp -f "../LICENSE" "${TEMP_DIR}"

for sdk in $IOS_PLATFORMS; do
    # Remove temporary install folders
    rm -r ${TEMP_DIR}/Release-${sdk}
done

# Remove build folder
rm -r "${SRCROOT}/${DERIVED_DATA_PATH}"