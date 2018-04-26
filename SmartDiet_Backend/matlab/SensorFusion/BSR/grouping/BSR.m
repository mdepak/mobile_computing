function [] = BSR( inFile, outFile )
%BSR Summary of this function goes here
%   Detailed explanation goes here

%% Compute globalPb and hierarchical segmentation for an example image.

addpath(fullfile(pwd,'lib'));

%% 1. compute globalPb on a BSDS image (5Gb of RAM required)
%clear all; close all; clc;
gPb_orient = globalPb(inFile, outFile);

%% 2. compute Hierarchical Regions

% for boundaries
% ucm = contours2ucm(gPb_orient, 'imageSize');
% imwrite(ucm,'/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_ucm.bmp');

% for regions 
ucm2 = contours2ucm(gPb_orient, 'doubleSize');
save('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/ucm2.mat','ucm2');

%% 3. usage example
%clear all;close all;clc;

%load double sized ucm
load('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/ucm2.mat','ucm2');

% convert ucm to the size of the original image
% ucm = ucm2(3:2:end, 3:2:end);

% get the boundaries of segmentation at scale k in range [0 1]
k = 0.4;
% bdry = (ucm >= k);

% get superpixels at scale k without boundaries:
labels2 = bwlabel(ucm2 <= k);
labels = labels2(2:2:end, 2:2:end);

% figure;imshow('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color.jpg');
% figure;imwrite(ucm, char('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_seg.jpg'));imshow(ucm);
% figure;imwrite(bdry, char('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_bdry.jpg'));imshow(bdry);
imwrite(mat2gray(labels), outFile);colormap(jet);

end

