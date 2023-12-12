package pwr.edu.ilab.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pwr.edu.ilab.ui.theme.GreatSailor
import pwr.edu.ilab.ui.theme.IbmPlexSansLight
import pwr.edu.ilab.ui.theme.MyRose
import pwr.edu.ilab.R

@Composable
fun Initial(navigateToLoginRegister: () -> Unit) {
    Card() {
        Image(
            painter = painterResource(id = R.drawable.ilab_banner),
            contentDescription = "Card Background",
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 360.dp, start = 20.dp, end = 20.dp),
            contentScale = ContentScale.FillWidth
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 250.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.dots),
                contentDescription = "Card Background",
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .padding(start = 150.dp, end = 150.dp, bottom = 50.dp, top = 5.dp),
                contentScale = ContentScale.FillWidth
            )

            Image(
                painter = painterResource(id = R.drawable.ilabs),
                contentDescription = "Card Background",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 150.dp, end = 150.dp),
                contentScale = ContentScale.FillWidth
            )
            Text(
                "Sieć najnowocześniejszych laboratoriów diagnostycznych w Polsce, oferująca ponad 250 badań",
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(top = 20.dp, bottom = 65.dp),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontFamily = IbmPlexSansLight,
                color = Color.Gray
            )

            Button(
                onClick = { navigateToLoginRegister() },
                shape = RoundedCornerShape(percent = 30),
                border = BorderStroke(0.dp, Color.Transparent),
                modifier = Modifier
                    .border(0.dp, Color.Transparent),
                colors = ButtonDefaults.buttonColors(backgroundColor = MyRose)
            ) {
                Text(
                    "Zadbaj o siebie ➢",
                    modifier = Modifier.padding(25.dp, 12.dp),
                    fontSize = 19.sp,
                    fontFamily = GreatSailor,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
    }
}
