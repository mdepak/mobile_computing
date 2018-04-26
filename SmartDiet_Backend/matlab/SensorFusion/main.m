reduce_image_sizes('/Users/student/Documents/Deepak/Mobile Computing/Git Repo/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/mc_data')
process_files('/Users/student/Documents/Deepak/Mobile Computing/Git Repo/mobile_computing/SmartDiet_Backend/matlab/SensorFusion/data/mc_data')

function get_ROF_file(thermal_image_file_path, output_file)
thermal_image = imread(char(thermal_image_file_path));
[Threshold, ROF] = extractROF(thermal_image,"");
ROF = logical(ROF);
imwrite(ROF,char(output_file))
end

function reduce_image_sizes(data_dir)
    for food = 1:40
    for img = 1:20
        color_file_name = (strcat(data_dir, "/", food+"_"+ img + "_color.jpg"));
        grey_file_name = (strcat(data_dir, "/", food+"_"+ img + "_grey.jpg"));
            imwrite(imresize(imread(color_file_name), 0.25), color_file_name);
            imwrite(imresize(imread(grey_file_name), 0.25), grey_file_name);
    end
    end
end

function process_files(data_dir)
for food = 1:40
    for img = 1:20
        color_file_name = (strcat(data_dir, "/", food+"_"+ img + "_color.jpg"));
        grey_file_name = (strcat(data_dir, "/", food+"_"+ img + "_grey.jpg"));
        thermal_rof_file  = (strcat(data_dir, "/", food+"_"+ img + "_thermal_rof.jpg"));
        bsr_rof_file  = (strcat(data_dir, "/", food+"_"+ img + "_bsr_rof.jpg"));
        
        out_file_name = (strcat(data_dir, "/", food+"_"+ img + "_color_out_grab_thermal.jpg"));
        
        get_ROF_file(grey_file_name,thermal_rof_file);
        
        addpath('./GrabCut/grabcut_complete');
        performGrabCut(color_file_name,thermal_rof_file, out_file_name);
        
        addpath('./BSR/grouping');
        BSR(color_file_name,bsr_rof_file)
        
       if exist(char(color_file_name), 'file') == 2
           disp("File found")
       end       
    end
end
end