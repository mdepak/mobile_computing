% Copyright (c) 2016 Arizona State University
% All rights reserved.
% Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
% 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
% 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
% 3. Neither the name of Arizona State University nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
% 
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

function [Threshold, ROF] = extractROF(thermal_img,fileName)
% The function is to get the region of interest of thermal image. Here, the
% region is the food portion. In the other words, the goal is to remove
% non-food portions such as background and plate based on the temperture threshold. 
%
% Input
% thermal_img: theraml image by thermal camera sensor
%
% Output
% Threshold: Not food portion temperture 
% ROI: Region Of Interest (food portion)
%
% In order to extract the region, I used the two methods: threshold and morphological operation.
% First of all, the threshold is below
% 1. Make a mask (9X9)
% 2. Diff_mat <- initialize as zero (row-mask_size X col-mask_size)
% 3. for i<-1 to row_size-mask_size
% 4.    for j-1 to col_size-mask_size
% 5.        Diff_mat(i,j) <- the differential between min and max in the mask
% 6. Find location(X,Y) which has a maximum in Diff_mat
% 7. Candidate <- thermal_img(X:X+mask_size, Y:Y+mask_size)
% 8. Threshold <- median(Candidate)
% Moreover, morphological operation is the close operation to remove salt 
% and pepper noise.

%% Threshold 
Mask_size = 3;
if size(thermal_img,3) ~= 1
    thermal_img = rgb2gray(thermal_img);
end

Diff_mat = zeros(size(thermal_img,1)-Mask_size, size(thermal_img,2)-Mask_size);

for i=1:size(thermal_img,1)-Mask_size
    for j=1:size(thermal_img,2)-Mask_size
        tmp = thermal_img(i:i+Mask_size-1, j:j+Mask_size-1);
        N_zero = find(tmp < 150);
        if size(N_zero,1) == 0
            Min = min(min(tmp));
            Max = max(max(tmp));
            Diff_mat(i,j) = Max-Min;
        end
    end
end

max_idx = find(Diff_mat == max(max(Diff_mat)));
if size(max_idx,1) > 1
    max_idx = max_idx(1);
end

max_Y = ceil(max_idx/size(Diff_mat,1));
max_X = mod(max_idx, size(Diff_mat,1));
Candidate = thermal_img(max_X:max_X+Mask_size-1, max_Y:max_Y+Mask_size-1);

Threshold = median(Candidate(:));



for i=1:size(thermal_img,1)
    for j=1:size(thermal_img,2)
        if thermal_img(i,j) < Threshold
            thermal_img(i,j) = 0;
        end
    end
end

% figure, imshow(thermal_img);

%% morphological operation
se = strel('disk',4);
ROF = imopen(thermal_img,se);
% 
% figure, imshow(ROF);

for i=1:size(ROF,1)
    for j=1:size(ROF,2)
        if ROF(i,j) > 0
            ROF(i,j) = 0;
        else
            ROF(i,j) = 1;
        end
    end
end


%thermal_image = imread(char('/Users/student/Downloads/group_21/1/1_grey.jpg'));
%[Threshold, ROF] = extractROF(thermal_image,"")
ROF = logical(ROF);
%figure, imshow(ROF);
%hold on;
% axis on;
% mkdir((strcat(pwd,'\Figures\Thermal_ROF\',num2str(Mask_size),'X',num2str(Mask_size))));
% saveas(gcf,strcat(pwd,'\Figures\Thermal_ROF\',num2str(Mask_size),'X',num2str(Mask_size),'\',num2str(fileName),'.jpg'));
% close all;

