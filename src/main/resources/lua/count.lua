local ans, has, cursor = {}, {}, "0";
repeat
    local t = redis.call("SCAN", cursor, "MATCH", KEYS[1], "COUNT", 1000);
    local list = t[2];
    for i = 1, #list do
        local s = list[i];
        if has[s] == nil then has[s] = 1; ans[#ans + 1] = s; end;
    end;
    cursor = t[1];
until cursor == "0";
return #ans; --or return ans;
