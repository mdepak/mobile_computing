function [outputArg1,outputArg2] = performGrabCut(original_image, polygon_file, out_file)
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here
global fixedBG;
global im;
global CurrRes;
global PrevRes;
PrevRes = CurrRes;

im = imread(char(original_image));
fixedBG = logical(imread(char(polygon_file)) < 128);

%%% show red bounds:
imBounds = im;
bounds = double(abs(edge(fixedBG)));
se = strel('square',3);
bounds = 1 - imdilate(bounds,se);
imBounds(:,:,2) = imBounds(:,:,2).*uint8(bounds);
imBounds(:,:,3) = imBounds(:,:,3).*uint8(bounds);

%{
figure,
imshow(im);
imshow(fixedBG);
imshow(imBounds);
hold on;
pause;
%}

imd = double(im);
% TODO: Modify beta and k values accordingly 
Beta = 0.5;
k = 4;
G = 50;
maxIter = 10;
diffThreshold = 0.001;
L = GCAlgo(imd, fixedBG,k,G,maxIter, Beta, diffThreshold,[]);
L = double(1 - L);

CurrRes = imd.*repmat(L , [1 1 3]);
for i=1:size(CurrRes,1)
    for j=1:size(CurrRes,2)
        if CurrRes(i,j,1) ==0 && CurrRes(i,j,2) ==0 && CurrRes(i,j,3) ==0
            CurrRes(i,j,:) = 255;
        end
    end
end

imwrite(uint8(CurrRes),char(out_file));
%figure,
%imshow(uint8(CurrRes));

disp("Grab cut completed");
end

