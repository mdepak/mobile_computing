function CurrRes = GrabCut( ROF, tp_img)
k = 5;
Beta = 0.13;
G = 50;
maxIter = 10;
diffThreshold = 0.001;
tp_img = imcrop(tp_img, [0 0 size(ROF,2) size(ROF,1)]);

imd = double(tp_img);
L = myGCAlgo(imd, ROF, k, G, maxIter, Beta, diffThreshold);
L = double(1 - L);

CurrRes = imd.*repmat(L , [1 1 3]);

% for i=1:size(CurrRes,1)
%     for j=1:size(CurrRes,2)
%         if CurrRes(i,j,1) ==0 && CurrRes(i,j,2) ==0 && CurrRes(i,j,3) ==0
%             CurrRes(i,j,:) = 255;
%         end
%     end
% end

CurrRes = uint8(CurrRes);
% figure, imshow(CurrRes);