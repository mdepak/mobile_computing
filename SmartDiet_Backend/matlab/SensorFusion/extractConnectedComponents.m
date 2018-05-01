function [outputArg1, outputArg2] = extractConnectedComponents(image_dir, image_file_name, outdir)
%UNTITLED Summary of this function goes here
%   Reads the file from the given path and writes the connected components
%   in the images as separate files

image = imread(char(strcat(image_dir, "/", image_file_name)));
BW_bw = im2bw(image, 0.4);

[L, num] = bwlabel(BW_bw);

for k = 1 : num
    thisBlob = ismember(L, k);
 
    b = zeros(size(image), 'uint8');
    
    for i = 1:3
        b(:, :, i) = uint8(image(:, :, i)) .* uint8(thisBlob);
    end
    image_out_file = strcat(char(outdir),"/", "seg_"+k+"_", char(image_file_name));
    imwrite(b, char(image_out_file));
    imshow(b);
end
end