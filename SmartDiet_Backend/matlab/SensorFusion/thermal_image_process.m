
thermal_image_file_path = '/Users/student/Downloads/group_21/2/20_grey.jpg'
%thermal_image_file_path = '/Users/student/Downloads/group_21/28/8_grey.jpg'

output_file = '/Users/student/Downloads/group_21/2/20_grey_rof.jpg'
%output_file = '/Users/student/Downloads/group_21/28/8_grey_rof.jpg'

get_ROF_file(thermal_image_file_path,output_file)

function get_ROF_file(thermal_image_file_path, output_file)
thermal_image = imread(char(thermal_image_file_path));
[Threshold, ROF] = extractROF(thermal_image,"");
% ROF = logical(ROF);
imwrite(~ROF,char(output_file))
figure,
imshow(ROF);

hold on;
end