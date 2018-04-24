% Copyright (c) 2016 Arizona State University
% All rights reserved.
% Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
% 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
% 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
% 3. Neither the name of Arizona State University nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
% 
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

function extraROF = extractSCF(ROF, edge_ROF)

Labeled_ROF = bwlabel(~ROF,8);
extraROF = zeros(size(edge_ROF,1), size(edge_ROF,2));
extraROF = extraROF + 1;

for i=1:size(unique(Labeled_ROF),1)-1
    Label_IDX = find(Labeled_ROF == i);
    
    Food_Portion_Match = zeros(size(Label_IDX,1),1);
    
    for j=1:size(Label_IDX,1)
        Food_Portion_Match(j,1) = edge_ROF(Label_IDX(j));
    end
    
    numNotFoodPixels = find(Food_Portion_Match == 0);
    if size(Label_IDX,1)*0.75 <= size(numNotFoodPixels,1)
       
       for k=1:size(Label_IDX,1)
           extraROF(Label_IDX(k)) = 0;
       end
       extraROF = ProProcessROF(extraROF);
        if size(find(extraROF == 0),1) < 3600
            extraROF = zeros(size(edge_ROF,1), size(edge_ROF,2));
            extraROF = extraROF + 1;
       else
           fprintf('There is the food portion which has the same color with the plate color\n');
       end
    end
end
