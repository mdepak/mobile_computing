% Copyright (c) 2016 Arizona State University
% All rights reserved.
% Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
% 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
% 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
% 3. Neither the name of Arizona State University nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
% 
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

function Proprocessed_ROF = ProProcessROF(ROF)

ROW_PROCESS = zeros(size(ROF,1), size(ROF,2));
COL_PROCESS = zeros(size(ROF,1), size(ROF,2));

for i=1:size(ROF,1)
    SP = 0;
    EP = 0;
    for j=1:size(ROF,2)
        if ROF(i,j) == 0
            if SP == 0
                SP = j;
            end
            EP = j;
        end
    end
    if SP ~= 0 && EP ~= 0
        ROW_PROCESS(i,SP:EP) = 1;
    end
end

for i=1:size(ROF,2)
    SP = 0;
    EP = 0;
    for j=1:size(ROF,1)
        if ROF(j,i) == 0
            if SP == 0
                SP = j;
            end
            EP = j;
        end
    end
    if SP ~= 0 && EP ~= 0
        COL_PROCESS(SP:EP,i) = 100;
    end
end
Proprocessed_ROF = ROW_PROCESS + COL_PROCESS;
for i=1:size(ROF,1)
    for j=1:size(ROF,2)
        if Proprocessed_ROF(i,j) == 101
            Proprocessed_ROF(i,j) = 0;
        else
            Proprocessed_ROF(i,j) = 1;
        end
    end
end
Proprocessed_ROF = logical(Proprocessed_ROF);
% figure, imshow(Proprocessed_ROF);



