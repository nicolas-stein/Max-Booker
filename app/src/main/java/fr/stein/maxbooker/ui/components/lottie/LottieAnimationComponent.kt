package fr.stein.maxbooker.ui.components.lottie

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.stein.maxbooker.R
import fr.stein.maxbooker.ui.theme.MaxBookerTheme

@Composable
fun LottieAnimationComponent(
    @RawRes animation: Int,
    modifier: Modifier = Modifier,
    loop: Boolean = false
) {
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animation)
    )

    val lottieProgress by animateLottieCompositionAsState(
        lottieComposition,
        iterations = if(loop) LottieConstants.IterateForever else 1,
        isPlaying = true
    )


    LottieAnimation(
        composition = lottieComposition,
        progress = { lottieProgress },
        modifier = modifier
    )
}

@Preview
@Composable
private fun LottieAnimationComponentPreview() {
    MaxBookerTheme {
        LottieAnimationComponent(
            animation = R.raw.lottie_login_loop,
            loop = true,
        )
    }
}