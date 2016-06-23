package io.cloudslang.content.jclouds.services.helpers;

import io.cloudslang.content.jclouds.entities.VolumeType;
import io.cloudslang.content.jclouds.entities.constants.Constants;
import io.cloudslang.content.jclouds.utils.InputsUtil;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.ec2.options.CreateVolumeOptions;
import org.jclouds.ec2.options.DetachVolumeOptions;

/**
 * Created by Mihai Tusa.
 * 6/22/2016.
 */
public class AmazonVolumeServiceHelper {
    private static final String GP2 = "gp2";
    private static final String IO1 = "io1";
    private static final String ST1 = "st1";
    private static final String SC1 = "sc1";
    private static final String STANDARD = "standard";


    private static final int FOUR = 4;
    private static final int FIVE_HUNDRED = 500;
    private static final int ONE_THOUSAND = 1024;
    private static final int TEN_THOUSANDS = 10000;

    public CreateVolumeOptions getCreateVolumeOptions(String snapshotId, String volumeType, int size, int iops, boolean encrypted) {
        validateVolumeTypeSizeIops(volumeType, size, iops);

        CreateVolumeOptions createVolumeOptions = CreateVolumeOptions.Builder
                .volumeType(getDefaultVolumeType(volumeType))
                .withSize(size)
                .withIops(iops)
                .isEncrypted(encrypted);

        if (StringUtils.isNotBlank(snapshotId)) {
            createVolumeOptions.fromSnapshotId(snapshotId);
        }

        return createVolumeOptions;
    }

    public DetachVolumeOptions getDetachVolumeOptions(String instanceId, String device) {
        if (StringUtils.isBlank(instanceId) && StringUtils.isBlank(device)) {
            return null;
        }

        DetachVolumeOptions detachVolumeOptions = new DetachVolumeOptions();
        if (StringUtils.isNotBlank(instanceId)) {
            detachVolumeOptions = DetachVolumeOptions.Builder.fromInstance(instanceId);
        }
        if (StringUtils.isNotBlank(device)) {
            detachVolumeOptions = DetachVolumeOptions.Builder.fromDevice(device);
        }

        return detachVolumeOptions;
    }

    private void validateVolumeTypeSizeIops(String volumeType, int size, int iops) {
        switch (volumeType) {
            case GP2:
                validateSize(GP2, Constants.ValidationValues.ONE, Constants.ValidationValues.COMMON_LARGE_VALUE, size);
                validateIops(GP2, Constants.ValidationValues.ONE_HUNDRED, TEN_THOUSANDS, iops);
                break;
            case IO1:
                validateSize(IO1, FOUR, Constants.ValidationValues.COMMON_LARGE_VALUE, size);
                validateIops(IO1, Constants.ValidationValues.ONE_HUNDRED, Constants.ValidationValues.TWENTY_THOUSANDS, iops);
                break;
            case ST1:
                validateSize(ST1, FIVE_HUNDRED, Constants.ValidationValues.COMMON_LARGE_VALUE, size);
                break;
            case SC1:
                validateSize(SC1, FIVE_HUNDRED, Constants.ValidationValues.COMMON_LARGE_VALUE, size);
                break;
            case STANDARD:
                validateSize(STANDARD, Constants.ValidationValues.ONE, ONE_THOUSAND, size);
                break;
            default:
                validateSize(getDefaultVolumeType(volumeType), Constants.ValidationValues.ONE, ONE_THOUSAND,
                        InputsUtil.getValidSize(String.valueOf(size)));
        }
    }

    private void validateSize(String input, int min, int max, int size) {
        if (size < min || size > max) {
            throw new RuntimeException("The size [" + String.valueOf(size) + "] provided for [" + input + "] volumeType " +
                    "should be greater or equal than [" + String.valueOf(min) + "] GiBs value and smaller or equal " +
                    "than [" + String.valueOf(max) + "] GiBs value.");
        }
    }

    private void validateIops(String input, int min, int max, int iops) {
        if (iops < min || iops > max) {
            throw new RuntimeException("The iops [" + String.valueOf(iops) + "] provided for [" + input + "] volumeType " +
                    "should be greater or equal than [" + String.valueOf(min) + "] IOPS value and smaller or equal " +
                    "than [" + String.valueOf(max) + "] IOPS value.");
        }
    }

    private String getDefaultVolumeType (String volumeType) {
        return (Constants.Miscellaneous.NOT_RELEVANT.equals(volumeType)) ? VolumeType.STANDARD.toString() : volumeType;
    }
}