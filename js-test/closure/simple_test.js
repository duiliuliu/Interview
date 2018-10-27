var gloableVar = "global variable";

(function outerFunc(outerArg) {
    var outerVar = "outer variable";

    (function innerFunc(innerArg) {
        var innerVar = "inner variable";

        console.log([outerArg, innerArg, outerVar, innerVar, gloableVar].join("\n"));
    })("param: innerArg")
})("param: outerArg")