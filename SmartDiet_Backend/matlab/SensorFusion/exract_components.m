
image = imread(char("2_5_color_out_grab_seg (1).jpg"));

BW_bw = im2bw(image,0.4);
class(image)
[L, num] = bwlabel(BW_bw);
for k = 1 : num
thisBlob = ismember(L, k);
figure

b = zeros(160,120,3,'uint8');
class(b)
for i = 1:3
    b(:,:,i) = uint8(image(:,:,i)) .* uint8(thisBlob);
end

imshow(b);
pause;
end