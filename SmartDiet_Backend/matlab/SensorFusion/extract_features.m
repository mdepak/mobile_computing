
image_file = '/Users/student/Downloads/group_21/2/20_grey_grabcut.jpg'

image = imread(char(image_file));
rgb_hist_feature = get_rgb_histogram(image);


disp("Size of the rgb hist feature");
disp(size(rgb_hist_feature));


hog_features = extractHOGFeatures(image);

disp("Size of the HOG feature");
disp(size(hog_features));

label_rgb_images();

function [rgb_hist_feature] = get_rgb_histogram(image)
    %Split into RGB Channels
Red = image(:,:,1);
Green = image(:,:,2);
Blue = image(:,:,3);

%Get histValues for each channel
[yRed, x] = imhist(Red, 32);
[yGreen, x] = imhist(Green, 32);
[yBlue, x] = imhist(Blue, 32);


rgb_hist_feature = [yRed; yGreen;  yBlue];

%Plot them together in one plot
% plot(x, yRed, 'Red', x, yGreen, 'Green', x, yBlue, 'Blue');
end

