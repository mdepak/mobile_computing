function [outputArg1, outputArg2] = constructDataSet(csv_file, images_dir, out_csv_file)
%constructDataSet Function used for constructing the dataset
% Used the labeled image csv file and write the feature matrix to another
% csv file
%

dataset = []

fid = fopen(csv_file);
out = textscan(fid,'%s%f','delimiter',',');
fclose(fid);

file_names = (out{1});
image_labels = out{2};

class(image_labels)

for i = 1: size(file_names, 1)
    image_name = [file_names{i,:}];
    label = int8(image_labels(i));
 
    image = imread(char(strcat(images_dir, "/", image_name)));
    rgb_hist_feature = get_rgb_histogram(image);
    hog_features = extractHOGFeatures(image);
    
    size(rgb_hist_feature')
    size(hog_features)
    size(label)
    
    sample = [rgb_hist_feature; hog_features'; label];
    dataset = [dataset; sample'];
end
csvwrite(out_csv_file, dataset)
end

function [rgb_hist_feature] = get_rgb_histogram(image)
%Split into RGB Channels
Red = image(:, :, 1);
Green = image(:, :, 2);
Blue = image(:, :, 3);

%Get histValues for each channel
[yRed, x] = imhist(Red, 32);
[yGreen, x] = imhist(Green, 32);
[yBlue, x] = imhist(Blue, 32);

rgb_hist_feature = [yRed; yGreen; yBlue];

%Plot them together in one plot
% plot(x, yRed, 'Red', x, yGreen, 'Green', x, yBlue, 'Blue');
end