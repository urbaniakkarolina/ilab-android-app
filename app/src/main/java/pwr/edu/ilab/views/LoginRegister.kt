package pwr.edu.ilab.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pwr.edu.ilab.R
import pwr.edu.ilab.ui.theme.LoginCardBackground
import pwr.edu.ilab.views.components.LoginRegisterCard


@Composable
fun LoginRegister(navigateToResults: () -> Unit, navigateToAssistantForm: () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.stain),
            contentDescription = "Card Background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ilabs),
                    contentDescription = "Card Background",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(bottom = 0.dp),
                    contentScale = ContentScale.FillWidth
                )

                Spacer(modifier = Modifier.padding(vertical = 20.dp))

                Card(
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .shadow(elevation=15.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White),
                    elevation = 15.dp
                ) {
                    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 50.dp)) {
                        LoginRegisterCard(navigateToResults, navigateToAssistantForm)
                    }
                }
            }
        }
    }
}