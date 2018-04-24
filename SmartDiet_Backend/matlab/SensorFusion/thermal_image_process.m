
base_folder_directory = '/Users/student/Documents/Deepak/Mobile Computing/Git Repo/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/'

thermal_image_file_path = strcat(base_folder_directory, 'data/20_grey.jpg');

output_file = strcat(base_folder_directory , 'data/101087_rof.jpg');

get_ROF_file(thermal_image_file_path,output_file);

color_image = strcat(base_folder_directory,'data/20_color.jpg');
output_seg_file = strcat(base_folder_directory, 'data/101087_seg.jpg')


grab_cut_input_comb = strcat(base_folder_directory,'data/101087_comb.jpg')
construct_grabcut_input(output_seg_file, output_file, grab_cut_input_comb)


function get_ROF_file(thermal_image_file_path, output_file)
thermal_image = imread(char(thermal_image_file_path));
[Threshold, ROF] = extractROF(thermal_image,"");
% ROF = logical(ROF);
imwrite(ROF,char(output_file))
figure,
imshow(ROF);

hold on;
end


% function get_image_seg(color_image, output_seg_file)
%  
% end

function construct_grabcut_input(segment_file, thermal_rof_file, grab_cut_comb_file)
ROF = imread(char(thermal_rof_file));
edge_ROF = imread(char(segment_file));

extraROF = extractSCF(ROF, edge_ROF);
imwrite(extraROF, char(grab_cut_comb_file))
end

