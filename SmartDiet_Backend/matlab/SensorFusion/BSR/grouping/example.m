%% Compute globalPb and hierarchical segmentation for an example image.

addpath(fullfile(pwd,'lib'));

%% 1. compute globalPb on a BSDS image (5Gb of RAM required)
clear all; close all; clc;

inFile = '/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color.jpg';

grey = '/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_grey.jpg';
imwrite(imresize(imread(grey), 0.25), grey);

outFile = '/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_gPb.mat';
img = imread(inFile);
newimg = imresize(img, 0.25);
imwrite(newimg, '/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color.jpg');
gPb_orient = globalPb(inFile, outFile);

%% 2. compute Hierarchical Regions

% for boundaries
ucm = contours2ucm(gPb_orient, 'imageSize');
imwrite(ucm,'/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_ucm.bmp');

% for regions 
ucm2 = contours2ucm(gPb_orient, 'doubleSize');
save('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_ucm2.mat','ucm2');

%% 3. usage example
clear all;close all;clc;

%load double sized ucm
load('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_ucm2.mat','ucm2');

% convert ucm to the size of the original image
ucm = ucm2(3:2:end, 3:2:end);

% get the boundaries of segmentation at scale k in range [0 1]
k = 0.4;
bdry = (ucm >= k);

% get superpixels at scale k without boundaries:
labels2 = bwlabel(ucm2 <= k);
labels = labels2(2:2:end, 2:2:end);

figure;imshow('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color.jpg');
figure;imwrite(ucm, char('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_seg.jpg'));imshow(ucm);
figure;imwrite(bdry, char('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_bdry.jpg'));imshow(bdry);
figure;imwrite(mat2gray(labels), char('/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/20_color_lbl.jpg'));imshow(labels, []);colormap(jet);

%% 4. compute globalPb on a large image:

% clear all; close all; clc;
% 
% imgFile = '/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/BSR/grouping/data/101087_big.jpg';
% outFile = '/Users/vinoth/codebase/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/BSR/grouping/data/101087_big_gPb.mat';
% 
% gPb_orient = globalPb_pieces(imgFile, outFile);
% delete(outFile);
% figure; imshow(max(gPb_orient,[],3)); colormap(jet);


%% 5. See also:
%
%   grouping/run_bsds500.m for reproducing our results on the BSDS500  
%
%   interactive/example_interactive.m for interactive segmentation
%
%   bench/test_benchs.m for an example on using the BSDS500 benchmarks

